package nl.naturalis.nba.etl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static nl.naturalis.nba.utils.StringUtil.lpad;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * A generic CSV extraction component taking raw CSV lines as input and outputting
 * {@code CSVRecordInfo} instances.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 */
public class CSVExtractor<T extends Enum<T>> implements Iterator<CSVRecordInfo<T>>, Iterable<CSVRecordInfo<T>> {

  /**
   * Thrown by a {@code CSVExtractor} if a client specified an invalid field number (less than zero or
   * greater than the number of fields in the CSV record).
   * 
   * @author Ayco Holleman
   *
   */
  public static class NoSuchFieldException extends ETLRuntimeException {

    private static final long serialVersionUID = 1L;

    static final String MSG = "Number of fields (%s) exceeds number of fields in CSV record (%s)";

    public NoSuchFieldException(Record record, int fieldNo) {
      super(String.format(MSG, fieldNo, record.getValues().length));
    }
  }

  private static final Logger logger = ETLRegistry.getInstance().getLogger(CSVExtractor.class);

  private final File csvFile;
  
  private CsvParserSettings settings;
  private CsvParser parser;
  private ParsingContext context;
  private Iterator<Record> iterator;
  
  private final ETLStatistics stats;
  private CsvFormat csvFormat;
  private char delimiter;
  private Charset charset;
  private boolean suppressErrors;
  private int numFields;

  /**
   * Creates a CSV extractor for the specified CSV file and updating the specified statistics object
   * as it extracts records from the file.
   * 
   * @param csvFile
   * @param stats
   */  
  public CSVExtractor(File csvFile, Class<? extends Enum<?>> csvClass, ETLStatistics stats) {
    checkEncoding();
    this.csvFile = csvFile;
    this.stats = stats;
    this.delimiter = '\t';
    this.charset = UTF_8;
    settings = new CsvParserSettings();
    settings.setSkipEmptyLines(true);
    setDelimiter(delimiter);
    this.numFields = csvClass.getEnumConstants().length;
  }

  /**
   * Whether to skip the first line in the CSV file.
   * 
   * @return
   */
  public boolean isSkipHeader() {
    return settings.isHeaderExtractionEnabled();
  }

  /**
   * Whether to skip the first line in the CSV file.
   * 
   * @param skipHeader
   */
  public void setSkipHeader(boolean skipHeader) {
    settings.setHeaderExtractionEnabled(skipHeader);
  }

  /**
   * Returns the format of the CSV file.
   * 
   * @return
   */
  public CsvFormat getCsvFormat() {
    return csvFormat;
  }

  /**
   * Sets the format of the CSV file.
   * 
   * @param csvFormat
   */
  public void setCsvFormat(CsvFormat csvFormat) {
    this.csvFormat = csvFormat;
  }

  /**
   * Returns the field separator. Default TAB ('\t').
   * 
   * @return
   */
  public char getDelimiter() {
    return settings.getFormat().getDelimiter();
  }

  /**
   * Sets the field separator.
   * 
   * @param delimiter
   */
  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
    settings.getFormat().setDelimiter(delimiter);
  }

  /**
   * Returns the character set of the CSV file.
   * 
   * @return
   */
  public Charset getCharset() {
    return charset;
  }

  /**
   * Sets the character set of the CSV file. Default UTF-8.
   * 
   * @param charset
   */
  public void setCharset(Charset charset) {
    this.charset = charset.equals(UTF_8) ? UTF_8 : charset;
  }

  /**
   * Returns the maximum number of characters to read in each column. The default is 4096 characters.
   * 
   * @return
   */
  public int getMaxCharsPerColumn() {
    return settings.getMaxCharsPerColumn();
  }

  /**
   * Set the maximum number of characters to read in each column.
   * 
   * @param maxCharsPerColumn
   */
  public void setMaxCharsPerColumn(int maxCharsPerColumn) {
    settings.setMaxCharsPerColumn(maxCharsPerColumn);
  }

  /**
   * Whether to suppress WARN and ERROR messages, but let through INFO messages. This might make log
   * files easier to read if you expect a lot of well-known, but not soon-to-be-solved errors.
   * 
   * @return
   */
  public boolean isSuppressErrors() {
    return suppressErrors;
  }

  /**
   * Suppress/enable error suppression.
   * 
   * @param suppressErrors
   */
  public void setSuppressErrors(boolean suppressErrors) {
    this.suppressErrors = suppressErrors;
  }

  @Override
  public Iterator<CSVRecordInfo<T>> iterator() {
    parser = new CsvParser(settings);
    iterator = parser.iterateRecords(csvFile).iterator();
    return this;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public CSVRecordInfo<T> next() {
    if (iterator == null)
      throw new IllegalStateException("Iterator not initialized");
    
      context = parser.getContext();
      Long lineNumber = context.currentLine(); // CurrentLine is the line that's up for parsing next
      Record record = iterator.next();
      if (record.getValues().length != numFields) {
        stats.badInput++;
        String msg = String.format("Number of fields (%s) does not match the required number of fields (%s). Line has been skipped.", record.getValues().length, numFields);
        logger.error(message(lineNumber, msg));
        return null;
      }
      CSVRecordInfo<T> csvRecord = new CSVRecordInfo<>(record, lineNumber);
      return csvRecord;
  }

  @Override
  public void remove() {
    throw new ETLRuntimeException("Not supported");
  }

  private static void checkEncoding() {
    /*
     * Make sure the JVM's default encoding is UTF-8. The main reason we want this to be the case is
     * that CSVParser.parse(String, CSVFormat) parses the String using the JVM's default encoding. Note
     * that for the CSVParser itself, it doesn't really matter whether the file encoding is UTF-8,
     * ISO-8995-1 or Cp1252, because all delimiters (end-of-field, end-of-record) are encoded
     * identically in these character sets, so tokenizing the CSV file won't be a problem. Nevertheless,
     * we JUST WANT THINGS TO BE UTF-8 ACROSS THE BOARD.
     */
    if (!Charset.defaultCharset().equals(UTF_8)) {
      logger.error("CSV imports require a default character encoding of UTF-8");
      logger.error("Please, add the following command line argument: -Dfile.encoding=UTF-8");
      String message = "Invalid default character encoding: " + Charset.defaultCharset().name();
      throw new ETLRuntimeException(message);
    }
  }

  private static String message(long line, String msg) {
    return "Line " + lpad(line, 6, '0', " | ") + msg;
  }

}
