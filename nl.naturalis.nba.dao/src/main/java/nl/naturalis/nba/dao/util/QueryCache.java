package nl.naturalis.nba.dao.util;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.naturalis.nba.api.QuerySpec;

/**
 * A LRU cache for NBA queries.
 * 
 * @author Ayco Holleman
 *
 * @param <V>
 */
public class QueryCache<V> extends LinkedHashMap<QuerySpec, V> {

	private final int maxSize;

	public QueryCache(int maxSize)
	{
		super(maxSize + 8, 1F, true);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<QuerySpec, V> eldest)
	{
		return size() > maxSize;
	}

}
