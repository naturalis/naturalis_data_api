package nl.naturalis.nba.dao.es.format.calc;

import java.util.Map;

import nl.naturalis.nba.dao.es.format.EntityObject;

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
	 * Initializes the map with the specified values. The keys of the map are
	 * argument names, the values of the map are argument values. This method is
	 * called just once on the calculator instance, right after it has been
	 * instantiated.
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
