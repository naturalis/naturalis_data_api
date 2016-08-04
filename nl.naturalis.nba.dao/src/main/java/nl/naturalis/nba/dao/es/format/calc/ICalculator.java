package nl.naturalis.nba.dao.es.format.calc;

import java.util.Map;

/**
 * An {@code ICalculator} is used to determine the value for a calculated field
 * in a data set. This type of field does not obtain its value directly from a
 * field in an Elasticsearch document. Instead, it applies some logic, with or
 * without using the data in the Elasticsearch document, to arrive at the value
 * for the data set field.
 * 
 * @author Ayco Holleman
 *
 */
public interface ICalculator {

	/**
	 * Calculates a values. The specified Elasticsearch document (converted to a
	 * {@code Map}) may or may not be used to calculate the value.
	 * 
	 * @param esDocumentAsMap
	 * @return
	 */
	Object calculateValue(Map<String, Object> esDocumentAsMap);

}
