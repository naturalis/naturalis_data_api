package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.format.calc.ICalculator;

public interface IDataSetFieldFactory {

	/**
	 * Returns an {@link IDataSetField} instance that retrieves its value
	 * directly a field in an Elasticsearch document. The {@code fieldName}
	 * argument specifies the name of the data set field (e.g. the header for a
	 * CSV column or the tag name of an XML element). The {@code path} argument
	 * specifies the full path of an Elasticsearch field. The path must be
	 * specified as a string array, with each element representing a
	 * successively deeper level in the Elasticsearch document. For example:<br>
	 * {@code identifications.0.defaultClassification.infraspecificEpithet}<br>
	 * As the above example shows, array access can also be encoded in the path
	 * array. See also {@link FieldConfigurator}.
	 * 
	 * @param fieldName
	 * @param path
	 * @return
	 */
	IDataSetField createDataField(String fieldName, String[] path);

	/**
	 * Returns an {@link IDataSetField} instance that provides a default value
	 * for the specified field.
	 * 
	 * @param fieldName
	 * @param constant
	 * @return
	 */
	IDataSetField createConstantField(String fieldName, String constant);

	/**
	 * Returns an {@link IDataSetField} instance that uses an
	 * {@link ICalculator} instance to calculate a value for the specified
	 * field.
	 * 
	 * @param fieldName
	 * @param calculator
	 * @return
	 */
	IDataSetField createdCalculatedField(String fieldName, ICalculator calculator);

}
