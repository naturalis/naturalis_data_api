package nl.naturalis.nba.dao.es.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nl.naturalis.nba.common.es.map.Mapping;

public class DataSetCollection {

	private Mapping source;
	private Map<String, Entity> entities;
	private Map<String, DataSet> dataSets;

	DataSetCollection()
	{
		entities = new HashMap<>();
		dataSets = new HashMap<>();
	}

	public Mapping getSource()
	{
		return source;
	}

	void setSource(Mapping source)
	{
		this.source = source;
	}

	public DataSet[] getDataSets()
	{
		Collection<DataSet> values = dataSets.values();
		return values.toArray(new DataSet[values.size()]);
	}

	void addDataSet(DataSet dataSet) throws DataSetConfigurationException
	{
		String name = dataSet.getName();
		if (dataSets.containsKey(name)) {
			throw new DataSetConfigurationException("Duplicate data set: " + name);
		}
		dataSets.put(name, dataSet);
	}

	public Entity[] getEntities()
	{
		Collection<Entity> values = entities.values();
		return values.toArray(new Entity[values.size()]);
	}

	void addEntity(Entity entity) throws DataSetConfigurationException
	{
		String name = entity.getName();
		if (entities.containsKey(name)) {
			throw new DataSetConfigurationException("Duplicate entity: " + name);
		}
		entities.put(name, entity);
	}

}
