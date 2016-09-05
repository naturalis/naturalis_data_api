package nl.naturalis.nba.dao.es.format.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.dao.es.format.DocumentFlattener;
import nl.naturalis.nba.dao.es.format.EntityObject;
import nl.naturalis.nba.dao.es.format.IField;

/**
 * Prints CSV records using {@link Map} objects as input. These {@code Map}
 * objects have supposedly been retrieved from Elasticsearch by calling
 * {@code SearchHit.getSource()}.
 * 
 * @author Ayco Holleman
 *
 */
public class CsvPrinter {

	private IField[] fields;
	private PrintStream ps;
	private DocumentFlattener flattener;

	public CsvPrinter(IField[] fields, DocumentFlattener flattener, OutputStream out)
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
		List<EntityObject> records = flattener.flatten(document);
		for (EntityObject record : records) {
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
