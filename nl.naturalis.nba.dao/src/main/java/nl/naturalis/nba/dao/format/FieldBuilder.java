package nl.naturalis.nba.dao.format;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.common.InvalidPathException;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.format.calc.VerbatimEventDateCalculator;
import nl.naturalis.nba.dao.format.config.FieldXmlConfig;
import nl.naturalis.nba.dao.format.config.PluginParamXmlConfig;

class FieldBuilder {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(FieldBuilder.class);

	/* Pick an arbitrary class from the package containing the calculators */
	private static String CALC_PACKAGE = VerbatimEventDateCalculator.class.getPackage().getName();

	private static String ERR_RELATIVE_PATH = "Relative path only allowed in combination with non-empty <path> within <data-source>";
	private static String ERR_EXCLUSIVE_ELEMENTS = "Only one of <path>, <constant>, <calculator> allowed within <field>";
	private static String ERR_MISSING_ELEMENT = "One of <path>, <constant>, <calculator> required within <field>";
	private static String ERR_NO_SUCH_CALCULATOR = "No such calculator: \"%s\". Please specify a valid Java class";
	private static String ERR_NOT_A_CALCULATOR = "Class %s does not implement ICalculator";
	private static String ERR_BAD_TERM = "Invalid term: %s";

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
		String name = fieldConfig.getName();
		URI term = getTerm(fieldConfig);
		Path path = new Path(fieldConfig.getPath().getValue());
		boolean relative = hasRelativePath(fieldConfig);
		if (dataSource.getMapping() != null) {
			validatePath(path, relative, name);
		}
		if (relative) {
			if (dataSource.getPath() == null) {
				throw new FieldConfigurationException(name, ERR_RELATIVE_PATH);
			}
			return fieldFactory.createEntityDataField(name, term, path, dataSource);
		}
		return fieldFactory.createDocumentDataField(name, term, path, dataSource);
	}

	IField createCalculatedField(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String name = fieldConfig.getName();
		URI term = getTerm(fieldConfig);
		String className = fieldConfig.getCalculator().getJavaClass();
		ICalculator calculator = getCalculator(name, className);
		Map<String, String> args = null;
		List<PluginParamXmlConfig> argConfigs = fieldConfig.getCalculator().getArg();
		if (argConfigs != null) {
			args = new HashMap<>(4);
			for (PluginParamXmlConfig argConfig : argConfigs) {
				args.put(argConfig.getName(), argConfig.getValue());
			}
			try {
				calculator.initialize(args);
			}
			catch (CalculatorInitializationException e) {
				throw new FieldConfigurationException(name, e.getMessage());
			}
		}
		return fieldFactory.createdCalculatedField(name, term, calculator);
	}

	IField createConstantField(FieldXmlConfig fieldConfig) throws FieldConfigurationException
	{
		String name = fieldConfig.getName();
		URI term = getTerm(fieldConfig);
		String value = fieldConfig.getConstant();
		return fieldFactory.createConstantField(name, term, value);
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
			else {
				fullPath = new Path();
				for (int i = 0; i < ds.getPath().countElements(); i++) {
					fullPath = fullPath.append(ds.getPath().element(i));
					if (fullPath.isArray(ds.getMapping())) {
						/*
						 * Append an arbitrary array index to the path so we can
						 * call Path.validate() later on
						 */
						fullPath = fullPath.append("0");
					}
				}
				fullPath = fullPath.append(path);
			}
			Path.validate(fullPath,ds.getMapping());
		}
		catch (InvalidPathException e) {
			throw new FieldConfigurationException(fieldName, e.getMessage());
		}
	}

	private static URI getTerm(FieldXmlConfig fieldConfig) throws FieldConfigurationException
	{
		String term = fieldConfig.getTerm();
		if (term == null)
			return null;
		try {
			return new URI(term);
		}
		catch (URISyntaxException e) {
			String msg = String.format(ERR_BAD_TERM, term);
			throw new FieldConfigurationException(fieldConfig.getName(), msg);
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