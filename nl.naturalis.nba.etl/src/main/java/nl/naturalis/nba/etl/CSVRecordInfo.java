package nl.naturalis.nba.etl;

import java.util.Arrays;

import com.univocity.parsers.common.record.Record;

/**
 * Immutable class wrapping a univocity-csv {@link Record} instance.
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 * @param <T> An enum class whose constants symbolize the fields in the CSV record. In other words
 *        the first constant symbolizes the first CSV field, the second constant symbolizes the
 *        second CSV field, etc. The enum class must have exactly as many constants as the number of
 *        CSV fields in the CSV record.
 */
public final class CSVRecordInfo<T extends Enum<T>> {

  private final Record record;
  private final long lineNumber;

  public CSVRecordInfo(Record record, long lineNumber) {
    this.record = record;
    this.lineNumber = lineNumber;
  }

  /**
   * Returns the value of the specified CSV field. Equivalent to calling {@code get(field, true)}.
   * 
   * @param field
   * @return
   */
  public String get(T field) {
    return get(field, true);
  }

  /**
   * Returns the value of the specified CSV field.
   * 
   * @param field The CSV field corresponding to the ordinal value of the specified enum constant.
   * @param emptyIsNull If {@code true}, the value of the field is whitespace-trimmed and, if a
   *        zero-length string remains, {@code null} is returned, otherwise the whitespace-trimmed
   *        string. If {@code false}, the value of the field is returned as-is.
   * @return
   */
  public String get(T field, boolean emptyIsNull) {
    int fieldNo = field.ordinal();
    if (emptyIsNull) {
      String s = record.getValue(fieldNo, "").trim();
      return s.length() == 0 ? null : s;
    }
    return record.getValue(fieldNo, "");
  }

  /**
   * Returns the plain values obtained from a parsed record parsed.
   * 
   * @return
   */
  public String getLine() {
    return Arrays.toString(record.getValues());
  }

  /**
   * Returns the line number of the line from which this instance was created.
   * 
   * @return
   */
  public long getLineNumber() {
    return lineNumber;
  }

}
