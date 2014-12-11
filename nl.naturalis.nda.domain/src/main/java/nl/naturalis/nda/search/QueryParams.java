package nl.naturalis.nda.search;

import java.util.*;

/**
 * A class capturing the input for an ElasticSearch query. This class mimicks
 * and is functionally equivalent to the {@code javax.ws.rs.core.MultiValuedMap}
 * interface within the JAX-RS framework. When passing URL query parameters in
 * to DAOs, the REST resource classes copy them from a {@code MultiValuedMap} to
 * a {@code QueryParams} object. This way the DAO project can avoid an awkward
 * dependency on the JAX-RS framework.
 * 
 * @author ayco_holleman
 */
@SuppressWarnings("serial")
public class QueryParams extends LinkedHashMap<String, List<String>> {

	/**
	 * Instantiate a {@code QueryParams} instance from another map. Although the
	 * constructor argument does not specify any concrete type of {@code Map},
	 * it really is presumed to be one of:
	 * <ol>
	 * <li>{@code javax.ws.rs.core.MultiValuedMap<String,String>}</li>
	 * <li>{@code Map<String,List<String>>}</li>
	 * <li>{@code Map<String,String>}</li>
	 * </ol>
	 * In the latter case the values are converted to a {@code List<String>>}.
	 * {@link ClassCastException}s for either keys or values are <i>not</i>
	 * trapped; the caller must make sure the constructor argument has the
	 * appropriate type arguments.
	 * 
	 * @see #addParams(Map)
	 * 
	 * @param map The {@code Map} to convert to a {@code QueryParams} object.
	 */
	public QueryParams(Map<?, ?> map)
	{
		addParams(map);
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


	public void addParams(Map<?, ?> params)
	{
		for (Object key : params.keySet()) {
			Object val = params.get(key);
			if (val instanceof String) {
				put((String) key, Arrays.asList((String) val));
			}
			else {
				put((String) key, (List<String>) params.get(key));
			}
		}
	}


	public void putSingle(String key, String value)
	{
		List<String> list = new ArrayList<>(4);
		list.add(value);
		put(key, list);
	}


	public void add(String key, String value)
	{
		List<String> values = get(key);
		if (values == null) {
			values = new ArrayList<>(4);
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


	public String getParam(String key)
	{
		String value = getFirst(key);
		if (value != null) {
			return value.length() == 0 ? null : value;
		}

		return null;
	}


	public String getParam(String key, String dfault)
	{
		List<String> values = get(key);
		if (values == null || values.size() == 0) {
			return null;
		}
		return values.get(0).length() == 0 ? dfault : values.get(0);
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


	public QueryParams copy()
	{
		return new QueryParams(this);
	}
}
