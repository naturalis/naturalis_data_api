package nl.naturalis.nda.elasticsearch.dao.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that mimicks and is functionally equivalent to
 * javax.ws.rs.core.MultiValuedMap<String,String>. Created so that the library
 * that this class is part of does not have a awkward dependence on the entire
 * JAX-RS framework just for something as generic as a map with multiple values
 * per key.
 * 
 * @author ayco_holleman
 * 
 */
@SuppressWarnings("serial")
public class MultiValuedProperties extends HashMap<String, List<String>> {

	/**
	 * Instantiate a MultiValuedProperties from another map, which <i>must</i>
	 * be a JAX-RS MultiValuedMap (or at least a Map<String,List<String>>).
	 * 
	 * @param multiValueMap
	 */
	public MultiValuedProperties(Map<?, ?> multiValueMap)
	{
		for (Object key : multiValueMap.keySet()) {
			List<String> values = (List<String>) multiValueMap.get(key);
			put((String) key, values);
		}
	}


	public MultiValuedProperties()
	{
		super();
	}


	public MultiValuedProperties(int initialCapacity, float loadFactor)
	{
		super(initialCapacity, loadFactor);
	}


	public MultiValuedProperties(int initialCapacity)
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
		List<String> values = null;
		if (containsKey(key)) {
			values = get(key);
		}
		if (values == null) {
			values = new ArrayList<String>(4);
			put(key, values);
		}
		values.add(value);
	}


	public String getFirst(String key)
	{
		if (containsKey(key)) {
			List<String> values = get(key);
			if (values.size() == 0) {
				return null;
			}
			return values.get(0);
		}
		return null;
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
