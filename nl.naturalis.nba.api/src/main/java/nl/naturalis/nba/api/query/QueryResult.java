package nl.naturalis.nba.api.query;

import java.util.Iterator;

/**
 * Java bean representing the result from a query request.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object returned by the query request.
 */
public class QueryResult<T> implements Iterable<T> {

	@Override
	public Iterator<T> iterator()
	{
		return null;
	}

}
