package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;
import nl.naturalis.nba.dao.es.format.config.FieldXmlConfig;

public class AbstractFieldBuilder {

	private static String CALC_PACKAGE = ICalculator.class.getPackage().getName();

	protected IFieldFactory fieldFactory;

	AbstractFieldBuilder(IFieldFactory fieldFactory)
	{
		this.fieldFactory = fieldFactory;
	}

	IField createCalculatedField(FieldXmlConfig fieldConfig) throws DataSetConfigurationException
	{
		String field = fieldConfig.getName();
		String className = fieldConfig.getCalculator().getJavaClass();
		ICalculator calculator = getCalculator(className);
		return fieldFactory.createdCalculatedField(field, calculator);
	}

	IField createConstantField(FieldXmlConfig fieldConfig)
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