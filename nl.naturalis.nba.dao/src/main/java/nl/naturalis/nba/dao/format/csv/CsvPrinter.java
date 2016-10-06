package nl.naturalis.nba.dao.format.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.DocumentFlattener;
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
public class CsvPrinter {

	private List<IField> fields;
	private PrintStream ps;

	public CsvPrinter(List<IField> fields, OutputStream out)
	{
		this.fields = fields;
		if (out instanceof PrintStream) {
			ps = (PrintStream) out;
		}
		else {
			ps = new PrintStream(out);
		}
	}

	public void printHeader()
	{
		for (int i = 0; i < fields.size(); ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(fields.get(i).getName());
		}
		ps.println();
	}

	public void printRecord(EntityObject entity) throws DataSetWriteException
	{
		for (int i = 0; i < fields.size(); ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(fields.get(i).getValue(entity));
		}
		ps.println();
	}

	public void flush()
	{
		ps.flush();
	}

}
