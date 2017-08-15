package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.dao.format.config.DataSetXmlConfig;

/**
 * A {@code DataSet} captures the information necessary to generate a dataset.
 * NB in other words instances of this class are not themselves containers of
 * dataset data; they contain the configuration driving the generation of a
 * dataset. A data set is a file or collection of files containing formatted
 * data (e.g. CSV records). Use a {@link DataSetBuilder} to get hold of a
 * {@code DataSet} instance. The {@code DataSet} class corresponds to the root
 * element of the XML configuration file for a dataset (&lt;dataset-config&gt;)
 * and is a beautified version of JAXB class {@link DataSetXmlConfig}.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSet {

	private DataSource sharedDataSource;
	private Entity[] entities;

	public DataSource getSharedDataSource()
	{
		return sharedDataSource;
	}

	void setSharedDataSource(DataSource sharedDataSource)
	{
		this.sharedDataSource = sharedDataSource;
	}

	public Entity[] getEntities()
	{
		return entities;
	}

	void setEntities(Entity[] entities)
	{
		this.entities = entities;
	}

	public Entity getEntity(String name) throws DataSetConfigurationException
	{
		for (Entity entity : entities) {
			if (entity.getName().equals(name)) {
				return entity;
			}
		}
		String msg = String.format("No such entity: \"%s\"", name);
		throw new DataSetConfigurationException(msg);
	}

}
