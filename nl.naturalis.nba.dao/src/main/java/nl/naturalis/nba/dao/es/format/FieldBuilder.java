package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

class FieldBuilder {

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	private FieldXmlConfig config;
	private DocumentType<?> dt;
	private String[] entityPath;
	private ITypedFieldFactory fieldFactory;

	FieldBuilder(FieldXmlConfig config, DocumentType<?> dt, String[] entityPath,
			ITypedFieldFactory fieldFactory)
	{
		this.config = config;
		this.dt = dt;
		this.entityPath = entityPath;
		this.fieldFactory = fieldFactory;
	}

	IField build() throws DataSetConfigurationException
	{
		String name = config.getName();
		if (name == null || name.trim().isEmpty()) {
			String msg = "Missing or empty <name> element within <field> element";
			throw new DataSetConfigurationException(msg);
		}
		IField field = null;
		if (config.getPath() != null) {
			field = createDataField();
		}
		if (config.getConstant() != null) {
			if (field != null) {
				String msg = "Only one of <path>, <constant>, <calculator> allowed";
				throw new DataSetConfigurationException(msg);
			}
			field = createConstantField();
		}
		if (config.getCalculator() != null) {
			if (field != null) {
				String msg = "Only one of <path>, <constant>, <calculator> allowed";
				throw new DataSetConfigurationException(msg);
			}
			field = createCalculatedField();
		}
		if (field == null) {

		}
		return field;
	}

	private IField createDataField() throws DataSetConfigurationException
	{
		String field = config.getName();
		String path = config.getPath().getValue();
		if (config.getPath().isRelative()) {
			if (entityPath == null) {
				String fmt = "Relative path (%s) only allowed in combination "
						+ "with non-empty <entity-object> element";
				String msg = String.format(fmt, path);
				throw new DataSetConfigurationException(msg);
			}
			return fieldFactory.createEntityDataField(dt, field, split(path));
		}
		return fieldFactory.createDocumentDataField(dt, field, split(path));
	}

	private IField createCalculatedField()
	{
		return null;
	}

	private IField createConstantField()
	{
		return null;
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

	private static String[] split(String path)
	{
		return path.split("\\.");
	}

}
