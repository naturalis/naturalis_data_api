package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

class TypedFieldBuilder {

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private Mapping source;
	private Path entityPath;
	private ITypedFieldFactory fieldFactory;

	TypedFieldBuilder(Mapping source, Path entityPath, ITypedFieldFactory fieldFactory)
	{
		this.source = source;
		this.entityPath = entityPath;
		this.fieldFactory = fieldFactory;
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
		String field = fieldConfig.getName();
		String path = fieldConfig.getPath().getValue();
		if (fieldConfig.getPath().isRelative()) {
			if (entityPath == null) {
				String fmt = "Relative path (%s) only allowed in combination "
						+ "with non-empty <entity-object> element";
				String msg = String.format(fmt, path);
				throw new DataSetConfigurationException(msg);
			}
			return fieldFactory.createEntityDataField(field, entityPath, source);
		}
		return fieldFactory.createDocumentDataField(field, entityPath, source);
	}

	private IField createCalculatedField(FieldXmlConfig fieldConfig)
			throws DataSetConfigurationException
	{
		String field = fieldConfig.getName();
		String className = fieldConfig.getCalculator().getJavaClass();
		ICalculator calculator = getCalculator(className);
		return fieldFactory.createdCalculatedField(field, calculator);
	}

	private IField createConstantField(FieldXmlConfig fieldConfig)
	{
		String field = fieldConfig.getName();
		String value = fieldConfig.getConstant();
		return fieldFactory.createConstantField(field, value);
	}

	private static ICalculator getCalculator(String name) throws DataSetConfigurationException
	{
		Class<?> cls;
		try {
			cls = Class.forName(CALC_PACKAGE + '.' + name);
		}
		catch (ClassNotFoundException e) {
			try {
				cls = Class.forName(name);
			}
			catch (ClassNotFoundException e2) {
				throw new DataSetConfigurationException("No such calculator: \"" + name + "\"");
			}
		}
		try {
			return (ICalculator) cls.newInstance();
		}
		catch (ClassCastException e) {
			throw new DataSetConfigurationException(name + " is not an ICalculator implementation");
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new DataSetConfigurationException(e);
		}
	}

}
