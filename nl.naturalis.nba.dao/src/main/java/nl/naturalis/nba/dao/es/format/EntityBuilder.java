package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.config.EntityXmlConfig;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

class EntityBuilder {

	private EntityXmlConfig config;
	private IDataSetFieldFactory fieldFactory;
	private DocumentType<?> defaultDocumentType;

	EntityBuilder(EntityXmlConfig config, IDataSetFieldFactory fieldFactory,
			DocumentType<?> defaultDocumentType)
	{
		this.config = config;
		this.fieldFactory = fieldFactory;
		this.defaultDocumentType = defaultDocumentType;
	}

	Entity build() throws DataSetConfigurationException
	{
		String name = config.getName();
		if (name == null || name.trim().isEmpty()) {
			String msg = "Missing or empty \"name\" attribute for element <entity>";
			throw new DataSetConfigurationException(msg);
		}
		Entity entity = new Entity();
		entity.setName(name);
		entity.setDocumentType(getDocumentType());
		entity.setPath(getPath());
		if (config.getQuerySpec() != null) {
			QuerySpecBuilder qsb = new QuerySpecBuilder(config.getQuerySpec());
			entity.setQuerySpec(qsb.build());
		}
		return entity;
	}

	private IDataSetField[] getFields() throws DataSetConfigurationException
	{
		if (config.getField().isEmpty()) {
			String fmt = "Entity %s: at least one field required";
			String msg = String.format(fmt, config.getName());
			throw new EntityConfigurationException(msg);
		}
		DocumentType<?> documentType = getDocumentType();
		if (documentType == null) {
			documentType = defaultDocumentType;
		}
		List<IDataSetField> fields = new ArrayList<>(config.getField().size());
		FieldBuilder fb;
		for (FieldXmlConfig fxc : config.getField()) {
			fb = new FieldBuilder(fxc, documentType, getPath(), fieldFactory);
			fields.add(fb.build());
		}
		return null;
	}

	private DocumentType<?> getDocumentType() throws DataSetConfigurationException
	{
		if (config.getSource() == null)
			return null;
		try {
			return DocumentType.forName(config.getSource());
		}
		catch (DaoException e) {
			String fmt = "Invalid value (\"%s\") in <source> element for entity %s. Please "
					+ "specify a valid Elasticsearch document type";
			String msg = String.format(fmt, config.getSource(), config.getName());
			throw new DataSetConfigurationException(msg);
		}
	}

	private String[] getPath()
	{
		if (config.getEntityObject() == null && config.getEntityObject().isEmpty()) {
			return null;
		}
		return config.getEntityObject().split("\\.");
	}

}
