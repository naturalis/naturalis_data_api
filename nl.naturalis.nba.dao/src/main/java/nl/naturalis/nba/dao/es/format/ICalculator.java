package nl.naturalis.nba.dao.es.format;

import java.util.Map;

/**
 * An {@code ICalculator} is used to determine the value for a calculated field
 * in a data set. This type of field does not obtain its value directly from an
 * Elasticsearch document. Instead, it applies some logic, with or without using
 * the data in the Elasticsearch document, to arrive at the value for the data
 * set field. {@code ICalculator} <i>must</i> provide a no-arg constructor.
 * 
 * @author Ayco Holleman
 *
 */
public interface ICalculator {

	/**
	 * Initializes the calculator with the specified values. The keys of the map
	 * are parameter names, the values of the map are parameter values. This
	 * method is called just once, right after instantiation of the calculator.
	 */
	void initialize(Map<String, String> args);

	/**
	 * Calculates a values. The specified Elasticsearch document (converted to a
	 * {@code Map}) may or may not be used to calculate the value.
	 * 
	 * @param entity
	 * @return
	 */
	Object calculateValue(EntityObject entity);

}
