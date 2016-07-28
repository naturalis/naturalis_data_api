package nl.naturalis.nba.dao.es.format.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

import nl.naturalis.nba.dao.es.format.IDataSetField;

public class CsvPrinter {

	private IDataSetField[] columns;
	private PrintStream ps;

	public CsvPrinter(IDataSetField[] columns, OutputStream out)
	{
		this.columns = columns;
		if (out instanceof PrintStream) {
			ps = (PrintStream) out;
		}
		else {
			ps = new PrintStream(out);
		}
	}

	public void printHeader()
	{
		for (int i = 0; i < columns.length; ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(columns[i].getName());
		}
		ps.println();
	}

	public void printRecord(Map<String, Object> data)
	{
		for (int i = 0; i < columns.length; ++i) {
			if (i != 0)
				ps.print(',');
			ps.print(columns[i].getValue(data));
		}
		ps.println();
	}

	public void flush()
	{
		ps.flush();
	}

}
