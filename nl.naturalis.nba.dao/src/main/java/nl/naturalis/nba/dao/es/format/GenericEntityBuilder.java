package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.config.EntityXmlConfig;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;
import nl.naturalis.nba.dao.es.format.config.QuerySpecXmlConfig;

class GenericEntityBuilder {

	private IGenericFieldFactory fieldFactory;

	GenericEntityBuilder(IGenericFieldFactory fieldFactory)
	{
		this.fieldFactory = fieldFactory;
	}

	Entity build(EntityXmlConfig entityConfig) throws DataSetConfigurationException
	{
		Entity entity = new Entity();
		entity.setName(getName(entityConfig));
		entity.setSource(ConfigUtil.getSource(entityConfig.getSource()));
		entity.setPath(getPath(entityConfig));
		entity.setQuerySpec(getQuerySpec(entityConfig));
		entity.setFields(getFields(entityConfig, entity));
		return entity;
	}

	private static String getName(EntityXmlConfig entityConfig) throws DataSetConfigurationException
	{
		String name = entityConfig.getName();
		if (name == null || name.trim().isEmpty()) {
			String msg = "Missing or empty \"name\" attribute for element <entity>";
			throw new DataSetConfigurationException(msg);
		}
		return name;
	}

	private static Path getPath(EntityXmlConfig entityConfig)
	{
		String pathString = entityConfig.getEntityObject();
		if (pathString == null || !pathString.isEmpty()) {
			return null;
		}
		return new Path(pathString);
	}

	private static QuerySpec getQuerySpec(EntityXmlConfig entityConfig)
			throws DataSetConfigurationException
	{
		QuerySpecXmlConfig querySpecConfig = entityConfig.getQuerySpec();
		if (querySpecConfig == null) {
			return null;
		}
		return new QuerySpecBuilder(querySpecConfig).build();
	}

	private IField[] getFields(EntityXmlConfig entityConfig, Entity entity)
			throws DataSetConfigurationException
	{
		if (entityConfig.getField().isEmpty()) {
			String fmt = "Entity %s: at least one field required";
			String msg = String.format(fmt, entityConfig.getName());
			throw new DataSetConfigurationException(msg);
		}
		Path path = entity.getPath();
		GenericFieldBuilder fb = new GenericFieldBuilder(path, fieldFactory);
		List<IField> fields = new ArrayList<>(entityConfig.getField().size());
		for (FieldXmlConfig fxc : entityConfig.getField()) {
			fields.add(fb.build(fxc));
		}
		return fields.toArray(new IField[fields.size()]);
	}

}
