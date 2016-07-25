package nl.naturalis.nba.dao.es.csv;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Map;

public class CsvPrinter {

	private IColumn[] columns;
	private PrintStream ps;

	public CsvPrinter(IColumn[] columns, OutputStream out)
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
			ps.print(columns[i].getHeader());
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
