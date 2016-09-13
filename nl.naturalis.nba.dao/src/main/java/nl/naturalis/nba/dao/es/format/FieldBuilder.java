package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.common.InvalidPathException;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

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
		if (fieldName == null) {
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
		Path path = new Path(fieldConfig.getPath().getValue());
		boolean relative = hasRelativePath(fieldConfig);
		if (dataSource.getMapping() != null) {
			validatePath(path, relative, fieldName);
		}
		if (relative) {
			if (dataSource.getPath() == null) {
				throw new FieldConfigurationException(fieldName, ERR_RELATIVE_PATH);
			}
			return fieldFactory.createEntityDataField(fieldName, path, dataSource);
		}
		return fieldFactory.createDocumentDataField(fieldName, path, dataSource);
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

	private void validatePath(Path path, boolean relative, String fieldName)
			throws FieldConfigurationException
	{
		DataSource ds = this.dataSource;
		try {
			Path fullPath;
			if (ds.getPath() == null || !relative) {
				fullPath = path;
			}
			else if (ds.getPath().isArray(ds.getMapping())) {
				/*
				 * If the entity object within the Elasticsearch document is an
				 * array, then to validate the entire path, we need to insert an
				 * arbitrary array index between the path to the entity object
				 * and the path to the field (relative to the entity object).
				 * For example, if the entity object is the "identifications"
				 * array within the Specimen document, then the only way we can
				 * verify if "defaultClassification" is a valid field, is by
				 * constructing the path
				 * "identifications.0.defaultClassification". The path
				 * "identifications.defaultClassification" would fail
				 * validation. See the validate method in the Path class.
				 */
				fullPath = ds.getPath().append("0").append(path);
			}
			else {
				fullPath = ds.getPath().append(path);
			}
			fullPath.validate(ds.getMapping());
		}
		catch (InvalidPathException e) {
			throw new FieldConfigurationException(fieldName, e.getMessage());
		}
	}

	private static boolean hasRelativePath(FieldXmlConfig fieldConfig)
	{
		if (fieldConfig.getPath().isRelative() == null)
			return false;
		return fieldConfig.getPath().isRelative();
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