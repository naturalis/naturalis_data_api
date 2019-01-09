package nl.naturalis.nba.etl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.naturalis.nba.utils.StringUtil.lchop;
import static nl.naturalis.nba.utils.StringUtil.lpad;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import com.univocity.parsers.common.TextParsingException;

import nl.naturalis.nba.utils.IOUtil;

/**
 * A generic CSV extraction component taking raw CSV lines as input and
 * outputting {@code CSVRecordInfo} instances.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
public class CSVExtractor<T extends Enum<T>> implements Iterator<CSVRecordInfo<T>>,
		Iterable<CSVRecordInfo<T>> {

	/**
	 * Thrown by a {@code CSVExtractor} if a client specified an invalid field
	 * number (less than zero or greater than the number of fields in the CSV
	 * record).
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static class NoSuchFieldException extends ETLRuntimeException {

	  private static final long serialVersionUID = 1L;
	  
		static final String MSG = "Specified field number (%s) exceeds number of fields in CSV record (%s)";

		public NoSuchFieldException(Record record, int fieldNo)
		{
		  super(String.format(MSG, fieldNo, record.getValues().length));
		}
	}

	private static final Logger logger = ETLRegistry.getInstance().getLogger(CSVExtractor.class);

	private final File csvFile;
	private final ETLStatistics stats;

	private boolean skipHeader;
	private CsvFormat csvFormat;
	private char delimiter;
	private Charset charset;
	private int maxCharsPerColumn = 4096;
	private boolean suppressErrors;

	private LineNumberReader lnr;
	private String line;

	/**
	 * Creates a CSV extractor for the specified CSV file and updating the
	 * specified statistics object as it extracts records from the file.
	 * 
	 * @param csvFile
	 * @param stats
	 */
	public CSVExtractor(File csvFile, ETLStatistics stats)
	{
		checkEncoding();
		this.csvFile = csvFile;
		this.stats = stats;
		this.delimiter = '\t';
		this.charset = UTF_8;
	}

	/**
	 * Whether to skip the first line in the CSV file.
	 * 
	 * @return
	 */
	public boolean isSkipHeader()
	{
		return skipHeader;
	}

	/**
	 * Whether to skip the first line in the CSV file.
	 * 
	 * @param skipHeader
	 */
	public void setSkipHeader(boolean skipHeader)
	{
		this.skipHeader = skipHeader;
	}

	/**
	 * Returns the format of the CSV file.
	 * 
	 * @return
	 */
	public CsvFormat getCsvFormat()
	{
		return csvFormat;
	}

	/**
	 * Sets the format of the CSV file.
	 * 
	 * @param csvFormat
	 */
	public void setCsvFormat(CsvFormat csvFormat)
	{
		this.csvFormat = csvFormat;
	}

	/**
	 * Returns the field separator. Default TAB ('\t').
	 * 
	 * @return
	 */
	public char getDelimiter()
	{
		return delimiter;
	}

	/**
	 * Sets the field separator.
	 * 
	 * @param delimiter
	 */
	public void setDelimiter(char delimiter)
	{
		this.delimiter = delimiter;
	}

	/**
	 * Returns the character set of the CSV file.
	 * 
	 * @return
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Sets the character set of the CSV file. Default UTF-8.
	 * 
	 * @param charset
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset.equals(UTF_8) ? UTF_8 : charset;
	}
	
	/**
	 * Returns the maximum number of characters to read in each column.
	 * The default is 4096 characters.
	 * 
	 * @return
	 */
	public int getMaxCharsPerColumn() {
    return maxCharsPerColumn;
  }

	/**
	 * Set the maximum number of characters to read in each column.
	 * 
	 * @param maxCharsPerColumn
	 */
  public void setMaxCharsPerColumn(int maxCharsPerColumn) {
    this.maxCharsPerColumn = maxCharsPerColumn;
  }

  /**
	 * Whether to suppress WARN and ERROR messages, but let through INFO
	 * messages. This might make log files easier to read if you expect a lot of
	 * well-known, but not soon-to-be-solved errors.
	 * 
	 * @return
	 */
	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	/**
	 * Suppress/enable error suppression.
	 * 
	 * @param suppressErrors
	 */
	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	@Override
	public Iterator<CSVRecordInfo<T>> iterator()
	{
		if (lnr != null)
			IOUtil.close(lnr);
		if (csvFormat == null) {
		  csvFormat = new CsvFormat();
		  csvFormat.setDelimiter(delimiter);
		}
		try {
			FileInputStream fis = new FileInputStream(csvFile);
			InputStreamReader isr = new InputStreamReader(fis, charset);
			lnr = new LineNumberReader(isr);
		}
		catch (IOException e) {
			String message = "Error instantiating iterator: " + e.getMessage();
			throw new ETLRuntimeException(message, e);
		}
		line = nextLine(lnr);
		if (line != null && skipHeader)
			line = nextLine(lnr);
		return this;
	}

	@Override
	public boolean hasNext()
	{
		return line != null;
	}

	@Override
	public CSVRecordInfo<T> next()
	{
		if (lnr == null || line == null) {
			throw new IllegalStateException("Iterator not initialized");
		}
		if (charset != UTF_8) {
			line = new String(line.getBytes(UTF_8));
		}
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setDelimiter(delimiter);
    settings.setMaxCharsPerColumn(maxCharsPerColumn);
    CsvParser parser = new CsvParser(settings);
    try {
      Record record = parser.parseRecord(line);      
      return new CSVRecordInfo<>(record, line, lnr.getLineNumber());
    }
    catch (Throwable t) {
      stats.badInput++;
      if (!suppressErrors) {
        String msg;
        if (t instanceof IOException)
          msg = message(lnr.getLineNumber(), lchop(t.getMessage(), "(line 1) "));
        else if (t instanceof TextParsingException)
          msg = message(lnr.getLineNumber(), "Line could not be parsed!");
        else
          msg = message(lnr.getLineNumber(), t.getMessage());
        logger.error(msg);
      }
      return null;
    }
    finally {
      line = nextLine(lnr);
    }
	}

	@Override
	public void remove()
	{
		throw new ETLRuntimeException("Not supported");
	}

	private static String nextLine(LineNumberReader lnr)
	{
		try {
			/*
			 * Return 1st non-empty line. Let's do first iteration outside loop.
			 * Empty lines won't happen that often.
			 */
			String line = lnr.readLine();
			if (line == null) { /* EOF */
				lnr.close();
				return null;
			}
			if (line.trim().length() != 0) {
				return line;
			}
			while (true) {
				if (logger.isDebugEnabled()) {
					logger.debug(message(lnr.getLineNumber(), "ignoring empty line"));
				}
				if ((line = lnr.readLine()) == null) {
					lnr.close();
					return null;
				}
				if (line.trim().length() != 0) {
					return line;
				}
			}
		}
		catch (IOException e) {
			IOUtil.close(lnr);
			throw new ETLRuntimeException(e);
		}
	}

	private static void checkEncoding()
	{
		/*
		 * Make sure the JVM's default encoding is UTF-8. The main reason we
		 * want this to be the case is that CSVParser.parse(String, CSVFormat)
		 * parses the String using the JVM's default encoding. Note that for the
		 * CSVParser itself, it doesn't really matter whether the file encoding
		 * is UTF-8, ISO-8995-1 or Cp1252, because all delimiters (end-of-field,
		 * end-of-record) are encoded identically in these character sets, so
		 * tokenizing the CSV file won't be a problem. Nevertheless, we JUST
		 * WANT THINGS TO BE UTF-8 ACROSS THE BOARD.
		 */
		if (!Charset.defaultCharset().equals(UTF_8)) {
			logger.error("CSV imports require a default character encoding of UTF-8");
			logger.error("Please, add the following command line argument: -Dfile.encoding=UTF-8");
			String message = "Invalid default character encoding: "
					+ Charset.defaultCharset().name();
			throw new ETLRuntimeException(message);
		}
	}

	private static String message(int line, String msg)
	{
		return "Line " + lpad(line, 6, '0', " | ") + msg;
	}

}
