package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.DocumentType;

public class DataSetCollection {

	private DocumentType dt;
	private String name;

	public DataSetCollection(DocumentType dt, String name)
	{
		this.dt = dt;
		this.name = name;
	}

	public DocumentType getDocumentType()
	{
		return dt;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof DataSetCollection))
			return false;
		DataSetCollection other = (DataSetCollection) obj;
		return name.equals(other.name) && dt == other.dt;
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + name.hashCode();
		hash = (hash * 31) + dt.hashCode();
		return hash;
	}

}
