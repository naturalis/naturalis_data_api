package nl.naturalis.nba.dao.es.format;

import java.io.File;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;

/**
 * A data set collection is a collection of {@link DataSet data
 * sets} that, to a large degree, are configured the same. This class captures a
 * data set's shared configuration. For example, all data sets in a data set
 * collection are comprised of the same {@link Entity files} and
 * for all data sets in that collection these files contain the same fields. You
 * can get hold of a {@code DataSetCollectionConfiguration} instance by using a
 * {@link DataSetCollectionConfigurationBuilder}. However, the configuration a
 * data set shares with other data sets is implicitly retrieved when using a
 * {@link DataSetConfigurationBuilder}.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSetCollectionConfiguration {

	private DocumentType<?> dt;
	private String name;
	private File home;
	private Entity[] entities;

	DataSetCollectionConfiguration()
	{
	}

	DataSetCollectionConfiguration(DocumentType<?> dt, String name)
	{
		this.dt = dt;
		this.name = name;
	}

	/**
	 * Returns the configuration for the specified entity.
	 */
	public Entity getEntityConfiguration(String entityName)
	{
		for (Entity entity : entities) {
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

	void setDocumentType(DocumentType<?> dt)
	{
		this.dt = dt;
	}

	/**
	 * Returns the name of this collection of data sets. In practice, the
	 * returned value is used as (actually inferred from) the name of the parent
	 * directory of a data set's {@link DataSet#getHome() home
	 * directory}.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	void setName(String name)
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

	void setHome(File home)
	{
		this.home = home;
	}

	/**
	 * Returns the {@link Entity entities} that all data sets
	 * belonging to this collection are comprisedof.
	 */
	public Entity[] getEntities()
	{
		return entities;
	}

	void setEntities(Entity[] entities)
	{
		this.entities = entities;
	}

}
