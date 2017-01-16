package nl.naturalis.nba.api;

import java.util.Iterator;

/**
 * Java bean representing the result from a search request.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object returned by the search request.
 */
public class SearchResult<T> implements Iterable<T> {

	@Override
	public Iterator<T> iterator()
	{
		return null;
	}

}
