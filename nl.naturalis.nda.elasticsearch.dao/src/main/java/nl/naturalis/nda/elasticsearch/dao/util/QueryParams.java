package nl.naturalis.nda.elasticsearch.dao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that mimicks and is functionally equivalent to
 * javax.ws.rs.core.MultiValuedMap<String,String>. The DAOs in this library can
 * be used outside and independently of the NDA REST framework, which is nice
 * for testing. Yet their prime purpose is to be used within it, so ES queries
 * are likely to come in the form of a URL's query string, converted into a
 * {@code MultiValuedMap}. The {@code QueryParams} class enables the DAO library
 * to be rid of an awkward depencency on the the entire JAX-RS framework, just
 * for something as generic as a map with multiple values per key.
 * 
 * @author ayco_holleman
 * 
 */
@SuppressWarnings("serial")
public class QueryParams extends HashMap<String, List<String>> {

	/**
	 * Instantiate a QueryParams instance from another map. Althoug the
	 * constructor argument does not specify any concrete type of {@code Map},
	 * it is actually presumed to be a JAX-RS MultiValuedMap, or at least a
	 * Map<String,List<String>>.
	 * 
	 * @param multiValuedMap
	 */
	public QueryParams(Map<?, ?> multiValuedMap)
	{
		for (Object key : multiValuedMap.keySet()) {
			List<String> values = (List<String>) multiValuedMap.get(key);
			put((String) key, values);
		}
	}


	public QueryParams()
	{
		super();
	}


	public QueryParams(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}


	public QueryParams(int initialCapacity)
	{
		super(initialCapacity);
	}


	public void putSingle(String key, String value)
	{
		List<String> list = new ArrayList<String>(4);
		list.add(value);
		put(key, list);
	}


	public void add(String key, String value)
	{
		List<String> values = get(key);
		if (values == null) {
			values = new ArrayList<String>(4);
			put(key, values);
		}
		values.add(value);
	}


	public String getFirst(String key)
	{
		List<String> values = get(key);
		if (values == null || values.size() == 0) {
			return null;
		}
		return values.get(0);
	}


	public String[][] keyValuePairs()
	{
		String[][] pairs = new String[keySet().size()][2];
		int i = 0;
		for (String key : keySet()) {
			List<String> list = get(key);
			String value = list != null && list.size() != 0 ? list.get(0) : null;
			pairs[i++] = new String[] { key, value };
		}
		return pairs;
	}

}
