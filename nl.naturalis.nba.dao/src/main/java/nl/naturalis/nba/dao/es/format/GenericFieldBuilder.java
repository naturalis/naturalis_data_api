package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

class GenericFieldBuilder extends AbstractFieldBuilder {

	private Path entityPath;

	GenericFieldBuilder(Path entityPath, IGenericFieldFactory fieldFactory)
	{
		super(fieldFactory);
		this.entityPath = entityPath;
	}

	IField build(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String name = fieldConfig.getName();
		if (name == null || name.trim().isEmpty()) {
			String msg = "Missing or empty <name> element within <field> element";
			throw new DataSetConfigurationException(msg);
		}
		IField field = null;
		if (fieldConfig.getPath() != null) {
			field = createDataField(fieldConfig);
		}
		if (fieldConfig.getConstant() != null) {
			if (field != null) {
				String msg = "Only one of <path>, <constant>, <calculator> allowed within <field> element";
				throw new DataSetConfigurationException(msg);
			}
			field = createConstantField(fieldConfig);
		}
		if (fieldConfig.getCalculator() != null) {
			if (field != null) {
				String msg = "Only one of <path>, <constant>, <calculator> allowed within <field> element";
				throw new DataSetConfigurationException(msg);
			}
			field = createCalculatedField(fieldConfig);
		}
		if (field == null) {
			String msg = "One of <path>, <constant>, <calculator> required within <field> element";
			throw new DataSetConfigurationException(msg);
		}
		return field;
	}

	private IField createDataField(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		IGenericFieldFactory fieldFactory = (IGenericFieldFactory) this.fieldFactory;
		String field = fieldConfig.getName();
		String path = fieldConfig.getPath().getValue();
		if (fieldConfig.getPath().isRelative()) {
			if (entityPath == null) {
				String fmt = "Relative path (%s) only allowed in combination "
						+ "with non-empty <entity-object> element";
				String msg = String.format(fmt, path);
				throw new DataSetConfigurationException(msg);
			}
			return fieldFactory.createEntityDataField(field, entityPath);
		}
		return fieldFactory.createDocumentDataField(field, entityPath);
	}

}
