package nl.naturalis.nba.dao.util;

import java.util.LinkedHashMap;

import nl.naturalis.nba.api.QuerySpec;

public class QueryCache<V> extends LinkedHashMap<QuerySpec, V> {

	private final int maxSize;

	public QueryCache(int maxSize)
	{
		super(maxSize + 1, 1F, true);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<QuerySpec, V> eldest)
	{
		return size() > maxSize;
	}

}
