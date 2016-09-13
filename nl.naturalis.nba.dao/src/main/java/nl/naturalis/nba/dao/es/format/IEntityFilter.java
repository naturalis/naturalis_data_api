package nl.naturalis.nba.dao.es.format;

import java.util.Map;

/**
 * An entity filter implements custom logic to determine if the documents coming
 * back from an Elasticsearch query must actually be used while writing a data
 * set.
 * 
 * @author Ayco Holleman
 *
 */
public interface IEntityFilter {

	/**
	 * Initializes the entity filter with the specified values. The keys of the
	 * map are parameter names, the values of the map are parameter values. This
	 * method is called just once, right after instantiation of the calculator.
	 */
	void initialize(Map<String, String> args);

	/**
	 * Whether or not to use the specified {@link EntityObject} while writing a
	 * data set.
	 * 
	 * @param entity
	 * @return
	 */
	boolean accept(EntityObject entity);

}
