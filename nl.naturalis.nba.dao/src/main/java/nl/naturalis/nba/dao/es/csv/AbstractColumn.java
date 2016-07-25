package nl.naturalis.nba.dao.es.csv;

public abstract class AbstractColumn implements IColumn {

	private String header;

	public AbstractColumn(String header)
	{
		this.header = header;
	}

	@Override
	public String getHeader()
	{
		return header;
	}

}
