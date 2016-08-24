package nl.naturalis.nba.dao.es.format.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.dao.es.format.DocumentFlattener;
import nl.naturalis.nba.dao.es.format.Entity;
import nl.naturalis.nba.dao.es.format.IDataSetField;

/**
 * Prints CSV records using {@link Map} objects as input. These {@code Map}
 * objects have supposedly been retrieved from Elasticsearch by calling
 * {@code SearchHit.getSource()}.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvPrinter {

	private IDataSetField[] fields;
	private PrintStream ps;
	private DocumentFlattener flattener;

	public CsvPrinter(IDataSetField[] fields, DocumentFlattener flattener, OutputStream out)
	{
		this.fields = fields;
		this.flattener = flattener;
		if (out instanceof PrintStream) {
			ps = (PrintStream) out;
		}
		else {
			ps = new PrintStream(out);
		}
	}

	public void printHeader()
	{
		for (int i = 0; i < fields.length; ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(fields[i].getName());
		}
		ps.println();
	}

	public void printRecord(Map<String, Object> document)
	{
		List<Entity> records = flattener.flatten(document);
		for (Entity record : records) {
			for (int i = 0; i < fields.length; ++i) {
				if (i != 0)
					ps.print(',');
				ps.print(fields[i].getValue(record));
			}
			ps.println();
		}
	}

	public void flush()
	{
		ps.flush();
	}

}
