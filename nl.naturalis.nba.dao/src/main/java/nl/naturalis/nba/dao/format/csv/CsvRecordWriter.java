package nl.naturalis.nba.dao.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IField;

/**
 * Prints CSV records using {@link Map} objects as input. These {@code Map}
 * objects have supposedly been retrieved from Elasticsearch by calling
 * {@code SearchHit.getSource()}.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvRecordWriter {

	private IField[] fields;
	private PrintStream ps;

	public CsvRecordWriter(IField[] fields, OutputStream out)
	{
		this.fields = fields;
		if (out instanceof PrintStream) {
			ps = (PrintStream) out;
		}
		else {
			ps = new PrintStream(out);
		}
	}

	public void printBOM()
	{
		ps.print('\ufeff');
	}

	public void printHeader()
	{
		for (int i = 0; i < fields.length; ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(escapeCsv(fields[i].getName()));
		}
		ps.println();
	}

	public void printRecord(EntityObject entity) throws DataSetWriteException
	{
		for (int i = 0; i < fields.length; ++i) {
			if (i != 0) {
				ps.print(',');
			}
			String val = fields[i].getValue(entity);
			// BEGIN HACK
			/*
			 * If Apache's commons-text correctly implements CSV escaping rules
			 * this should not be necessary, but it seems like GBIF cannot
			 * handle newlines in CSV records.
			 */
			// END HACK
			//val = val.replace("\n", "\\n");
			val = escapeCsv(val);
			ps.print(val);
		}
		ps.println();
	}

	public void flush()
	{
		ps.flush();
	}

}
