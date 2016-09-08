package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;
import nl.naturalis.nba.dao.es.format.config.PathXmlConfig;

class FieldBuilder {

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();
	private static String ERR_RELATIVE_PATH = "Relative path only allowed in combination with non-empty <path> within <data-source>";
	private static String ERR_EXCLUSIVE_ELEMENTS = "Only one of <path>, <constant>, <calculator> allowed within <field>";
	private static String ERR_MISSING_ELEMENT = "One of <path>, <constant>, <calculator> required within <field>";
	private static String ERR_NO_SUCH_CALCULATOR = "No such calculator: \"%s\". Please specify a valid Java class";
	private static String ERR_NOT_A_CALCULATOR = "Class %s does not implement ICalculator";

	private IFieldFactory fieldFactory;
	private DataSource dataSource;

	FieldBuilder(IFieldFactory fieldFactory, DataSource dataSource)
	{
		this.fieldFactory = fieldFactory;
		this.dataSource = dataSource;
	}

	IField build(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String fieldName = fieldConfig.getName();
		if (fieldName == null || fieldName.trim().isEmpty()) {
			String msg = "Missing or empty <name> element within <field> element";
			throw new DataSetConfigurationException(msg);
		}
		IField field = null;
		if (fieldConfig.getPath() != null) {
			field = createDataField(fieldConfig);
		}
		if (fieldConfig.getConstant() != null) {
			if (field != null) {
				throw new FieldConfigurationException(fieldName, ERR_EXCLUSIVE_ELEMENTS);
			}
			field = createConstantField(fieldConfig);
		}
		if (fieldConfig.getCalculator() != null) {
			if (field != null) {
				throw new FieldConfigurationException(fieldName, ERR_EXCLUSIVE_ELEMENTS);
			}
			field = createCalculatedField(fieldConfig);
		}
		if (field == null) {
			throw new FieldConfigurationException(fieldName, ERR_MISSING_ELEMENT);
		}
		return field;
	}

	IField createDataField(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String fieldName = fieldConfig.getName();
		Path path = getPath(fieldConfig);
		if (dataSource.getPath() == null)
			return fieldFactory.createDocumentDataField(fieldName, path, dataSource);
		return fieldFactory.createEntityDataField(fieldName, path, dataSource);
	}

	IField createCalculatedField(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String fieldName = fieldConfig.getName();
		String className = fieldConfig.getCalculator().getJavaClass();
		ICalculator calculator = getCalculator(fieldName, className);
		return fieldFactory.createdCalculatedField(fieldName, calculator);
	}

	IField createConstantField(FieldXmlConfig fieldConfig) throws FieldConfigurationException
	{
		String field = fieldConfig.getName();
		String value = fieldConfig.getConstant();
		return fieldFactory.createConstantField(field, value);
	}

	private Path getPath(FieldXmlConfig fieldConfig) throws FieldConfigurationException
	{
		PathXmlConfig path = fieldConfig.getPath();
		if (path.isRelative() != null && path.isRelative() && dataSource.getPath() == null) {
			String fieldName = fieldConfig.getName();
			throw new FieldConfigurationException(fieldName, ERR_RELATIVE_PATH);
		}
		return new Path(path.getValue());
	}

	private static ICalculator getCalculator(String fieldName, String className)
			throws DataSetConfigurationException
	{
		Class<?> cls;
		try {
			cls = Class.forName(CALC_PACKAGE + '.' + className);
		}
		catch (ClassNotFoundException e) {
			try {
				cls = Class.forName(className);
			}
			catch (ClassNotFoundException e2) {
				String msg = String.format(ERR_NO_SUCH_CALCULATOR, className);
				throw new FieldConfigurationException(fieldName, msg);
			}
		}
		try {
			return (ICalculator) cls.newInstance();
		}
		catch (ClassCastException e) {
			String msg = String.format(ERR_NOT_A_CALCULATOR, className);
			throw new FieldConfigurationException(fieldName, msg);
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new FieldConfigurationException(fieldName, e.getMessage());
		}
	}
}