package nl.naturalis.nda.elasticsearch.load;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.domainobject.util.IOUtil;
import static org.domainobject.util.StringUtil.*;
import org.slf4j.Logger;

import static nl.naturalis.nda.elasticsearch.load.CSVImportUtil.*;

/**
 * A generic CSV extraction component taking raw CSV lines as input and
 * outputting commons-csv {@code CSVRecordInfo} instances.
 * 
 * @author Ayco Holleman
 *
 */
public class CSVExtractor implements Iterator<CSVRecordInfo>, Iterable<CSVRecordInfo> {

	/**
	 * Thrown by a {@code CSVExtractor} if a client specified an invalid field
	 * number (less than zero or greater than the number of fields in the CSV
	 * record).
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static class NoSuchFieldException extends RuntimeException {
		static final String MSG = "Specified field number (%s) exceeds number of fields in CSV record (%s)";
		public NoSuchFieldException(CSVRecord record, int fieldNo)
		{
			super(String.format(MSG, fieldNo, record.size()));
		}
	}

	private static final Logger logger = Registry.getInstance().getLogger(CSVExtractor.class);

	private final File csvFile;
	private final ETLStatistics stats;

	private boolean skipHeader;
	private CSVFormat csvFormat;
	private char delimiter;
	private Charset charset;
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
	public CSVFormat getCsvFormat()
	{
		return csvFormat;
	}

	/**
	 * Sets the format of the CSV file.
	 * 
	 * @param csvFormat
	 */
	public void setCsvFormat(CSVFormat csvFormat)
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
	public Iterator<CSVRecordInfo> iterator()
	{
		if (lnr != null)
			IOUtil.close(lnr);
		if (csvFormat == null)
			csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter);
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
	public CSVRecordInfo next()
	{
		if (lnr == null || line == null) {
			throw new IllegalStateException("Iterator not initialized");
		}
		if (charset != UTF_8)
			line = new String(line.getBytes(UTF_8));
		try {
			CSVParser parser = CSVParser.parse(line, csvFormat);
			CSVRecord record = (CSVRecord) nextRecordMethod.invoke(parser);
			return new CSVRecordInfo(record, line, lnr.getLineNumber());
		}
		catch (Throwable t) {
			stats.badInput++;
			if (!suppressErrors) {
				// Seriously lame, but so thinks common-csv itself (see comments
				// inside their source code)
				while (t.getCause() != null)
					t = t.getCause();
				String msg;
				if (t instanceof IOException)
					msg = getDefaultMessagePrefix(lnr.getLineNumber(), "?") + lchop(t.getMessage(), "(line 1) ");
				else
					msg = getDefaultMessagePrefix(lnr.getLineNumber(), "?") + t.getMessage();
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
			// Let's do first iteration outside loop. Empty lines won't happen
			// that often.
			String line = lnr.readLine();
			if (line == null) {
				lnr.close();
				return null;
			}
			if (line.trim().length() != 0) {
				return line;
			}
			while (true) {
				if (logger.isDebugEnabled()) {
					String msg = getDefaultMessagePrefix(lnr.getLineNumber(), "?") + "ignoring empty line";
					logger.debug(msg);
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
			String message = "Invalid default character encoding: " + Charset.defaultCharset().name();
			throw new ETLRuntimeException(message);
		}
	}

	/*
	 * All this reflection (see below) is pretty lame, but given the current
	 * state of commons-csv it seemed like the best option.
	 */

	private static final Method nextRecordMethod;

	static {
		try {
			nextRecordMethod = CSVParser.class.getDeclaredMethod("nextRecord");
			nextRecordMethod.setAccessible(true);
		}
		catch (Throwable t) {
			throw new ETLRuntimeException(t);
		}
	}
}
