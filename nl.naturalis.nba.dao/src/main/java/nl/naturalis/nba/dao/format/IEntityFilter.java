package nl.naturalis.nba.dao.format;

import java.util.Map;

/**
 * An entity filter implements custom logic to determine if the
 * {@link EntityObject entity objects} produced by the {@link DocumentFlattener}
 * must be written to the dataset. This allows you to filter out entire
 * documents that would be too cumbersome to filter out using a
 * {@link QuerySpec} (specified through the &lt;query-spec&gt; element in the
 * XML configuration file for a dataset). But, more importantly, it allows you
 * to filter out specific objects within a document, which can never be achieved
 * through an Elasticsearch query.
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
	void initialize(Map<String, String> args) throws EntityFilterInitializationException;

	/**
	 * Whether or not to use the specified {@link EntityObject} while writing a
	 * data set.
	 * 
	 * @param entity
	 * @return
	 */
	boolean accept(EntityObject entity) throws EntityFilterException;

}
