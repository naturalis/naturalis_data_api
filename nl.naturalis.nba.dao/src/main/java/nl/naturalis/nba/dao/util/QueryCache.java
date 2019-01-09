package nl.naturalis.nba.dao.util;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * A LRU cache for NBA queries.
 * 
 * @author Ayco Holleman
 *
 * @param <V>
 */
public class QueryCache<V> extends LinkedHashMap<QuerySpec, V> {
  
  private static final long serialVersionUID = 1L;

	private static Logger logger = getLogger(QueryCache.class);

	private final int maxSize;

	public QueryCache(int maxSize)
	{
		super(maxSize + 8, 1F, true);
		this.maxSize = maxSize;
	}

	@Override
	public V put(QuerySpec key, V value)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Caching query: {}", JsonUtil.toJson(key));
		}
		return super.put(key, value);
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<QuerySpec, V> eldest)
	{
		if (size() > maxSize) {
			if (logger.isDebugEnabled()) {
				logger.debug("Evicting query: {}", JsonUtil.toJson(eldest.getKey()));
			}
			return true;
		}
		return false;
	}

}
