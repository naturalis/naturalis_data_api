package nl.naturalis.nba.dao.es.format;

import java.io.File;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;

/**
 * A {@code DataSetCollection} is a collection of {@link DataSet data sets} that
 * use the same {@link IDataSetField fields}. For example, all data sets in the
 * DwCA zoology collection have exactly the same fields in their occurrence.txt
 * file, while data sets in the geology collection use a different set of
 * fields. A data set may be comprised of multiple files. For example the DwCA
 * files for taxa includes a taxa.txt file, and may additionally include a
 * references.txt file, a distribution.txt file, etc. These files are referred
 * to as entities and are represented by the {@link DataSetEntity} class. All
 * data sets in a collection are comprised of the same entities. A
 * {@code DataSetCollection} is itself subsumed under an Elasticsearch
 * {@link DocumentType document type}. In other words, all data sets in a given
 * collection draw their data from the same Elasticsearch document type. As with
 * the {@link DataSet} and {@link DataSetEntity} classes, this class only
 * provides <i>meta data</i> about a data set collection. It is not concerned
 * with the data itself.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetCollection {

	private DocumentType<?> dt;
	private String name;
	private File home;
	private DataSetEntity[] entities;

	public DataSetCollection()
	{
	}

	public DataSetCollection(DocumentType<?> dt, String name)
	{
		this.dt = dt;
		this.name = name;
	}

	public DataSetEntity getEntity(String entityName)
	{
		for (DataSetEntity entity : entities) {
			if (entity.getName().equals(entityName)) {
				return entity;
			}
		}
		String fmt = "No entity named \"%s\" has been defined for data set collection %s";
		String msg = String.format(fmt, entityName, this.name);
		throw new DaoException(msg);
	}

	/**
	 * Returns the Elasticsearch {@link DocumentType document type} from which
	 * data for this collection of data sets is sourced.
	 * 
	 * @return
	 */
	public DocumentType<?> getDocumentType()
	{
		return dt;
	}

	public void setDocumentType(DocumentType<?> dt)
	{
		this.dt = dt;
	}

	/**
	 * Returns the name of this collection of data sets. In practice, the
	 * returned value is used as (actually inferred from) the name of the parent
	 * directory of a data set's {@link DataSet#getHome() home directory}.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the home directory for this data set collection. This is where
	 * data set writers will expect to find configuration data for the data set.
	 */
	public File getHome()
	{
		return home;
	}

	public void setHome(File home)
	{
		this.home = home;
	}

	/**
	 * Returns the {@link DataSetEntity entities} that all data sets belonging
	 * to this collection are comprisedof.
	 */
	public DataSetEntity[] getEntities()
	{
		return entities;
	}

	public void setEntities(DataSetEntity[] entities)
	{
		this.entities = entities;
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
