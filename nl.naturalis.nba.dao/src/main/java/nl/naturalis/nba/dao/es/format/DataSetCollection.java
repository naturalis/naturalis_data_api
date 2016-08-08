package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.DocumentType;

/**
 * A {@code DataSetCollection} is a collection of data sets that use the same
 * {@link IDataSetField fields}. In other words, they are configured using the
 * same "fields.config" file (see {@link FieldConfigurator}). A
 * {@code DataSetCollection} is itself subsumed under a particular Elasticsearch
 * {@link DocumentType document type}. In other words, all data sets in a given
 * collection draw their data from the same document type.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetCollection {

	private DocumentType dt;
	private String name;

	public DataSetCollection(DocumentType dt, String name)
	{
		this.dt = dt;
		this.name = name;
	}

	/**
	 * Returns the Elasticsearch {@link DocumentType document type} from which
	 * data for this collection of data sets is sourced.
	 * 
	 * @return
	 */
	public DocumentType getDocumentType()
	{
		return dt;
	}

	/**
	 * Returns the name of this collection of data sets.
	 * 
	 * @return
	 */
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
