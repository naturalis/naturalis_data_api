package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.List;

/**
 * Class capturing the information necessary to generate a data set. A data set
 * is a file or collection of files containing formatted data (e.g. CSV
 * records). Use a {@link DataSetConfigurationBuilder} to get hold of a
 * {@code DataSetConfiguration} instance.
 * 
 * @author Ayco Holleman
 *
 */
public class DataSet {

	private List<Entity> entities;

	public List<Entity> getEntities()
	{
		return entities;
	}

	public Entity getEntity(String name) throws DataSetConfigurationException
	{
		for (Entity entity : entities) {
			if (entity.getName().equals(name))
				return entity;
		}
		String msg = String.format("No such entity: \"%s\"", name);
		throw new DataSetConfigurationException(msg);
	}

	void addEntity(Entity entity)
	{
		if (entities == null)
			entities = new ArrayList<>(5);
		entities.add(entity);
	}

}
