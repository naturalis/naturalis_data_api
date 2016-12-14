package nl.naturalis.nba.dao.util.es;

import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.NbaException;

/**
 * Generic interface for processing an Elastichsearch {@link SearchHit} object.
 * 
 * @author Ayco Holleman
 *
 */
public interface SearchHitHandler {

	/**
	 * Processes an Elastichsearch {@link SearchHit} object.
	 * 
	 * @param hit
	 * @throws NbaException
	 */
	boolean handle(SearchHit hit) throws NbaException;

}
