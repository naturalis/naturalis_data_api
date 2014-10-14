package nl.naturalis.nda.elasticsearch.dao.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class QueryParams extends HashMap<String, List<String>> {

    /**
     * Instantiate a {@code QueryParams} instance from another map. Although the
     * constructor argument does not specify any concrete type of {@code Map},
     * it really is presumed to be either a
     * {@code javax.ws.rs.core.MultiValuedMap<String,String>} or a
     * {@code Map<String,List<String>>} or a {@code Map<String,String>}. In the
     * latter case the values are converted to a {@code List<String>>}.
     * {@link ClassCastException}s are not trapped; the caller has to make sure
     * the constructor argument has the appropriate type.
     *
     * @param map The {@code Map} to convert to a {@code QueryParams} object.
     */
    public QueryParams(Map<?, ?> map) {
        for (Object key : map.keySet()) {
            Object val = map.get(key);
            if (val instanceof String) {
                put(key.toString(), Arrays.asList((String) val));
            } else {
                put(key.toString(), (List<String>) map.get(key));
            }
        }
    }

    /**
     * Instantiate a {@code QueryParams} instance from two other maps. See
     * {@link #QueryParams(Map)}. This constructor can be used to construct the
     * instance from both query parameters and path parameters. See javadoc for
     * javax.ws.rs.core.MultiValuedMap.
     *
     * @param map0
     * @param map1
     */
    public QueryParams(Map<?, ?> map0, Map<?, ?> map1) {
        for (Object key : map0.keySet()) {
            Object val = map0.get(key);
            if (val instanceof String) {
                put((String) key, Arrays.asList((String) val));
            } else {
                put((String) key, (List<String>) map0.get(key));
            }
        }
        for (Object key : map1.keySet()) {
            Object val = map1.get(key);
            if (val instanceof String) {
                put((String) key, Arrays.asList((String) val));
            } else {
                put((String) key, (List<String>) map1.get(key));
            }
        }
    }

    public QueryParams() {
        super();
    }

    public QueryParams(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public QueryParams(int initialCapacity) {
        super(initialCapacity);
    }

    public void putSingle(String key, String value) {
        List<String> list = new ArrayList<String>(4);
        list.add(value);
        put(key, list);
    }

    public void add(String key, String value) {
        List<String> values = get(key);
        if (values == null) {
            values = new ArrayList<String>(4);
            put(key, values);
        }
        values.add(value);
    }

    public String getFirst(String key) {
        List<String> values = get(key);
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0);
    }

    public String getParam(String key) {
        List<String> values = get(key);
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0).length() == 0 ? null : values.get(0);
    }

    public String getParam(String key, String dfault) {
        List<String> values = get(key);
        if (values == null || values.size() == 0) {
            return null;
        }
        return values.get(0).length() == 0 ? dfault : values.get(0);
    }

    public String[][] keyValuePairs() {
        String[][] pairs = new String[keySet().size()][2];
        int i = 0;
        for (String key : keySet()) {
            List<String> list = get(key);
            String value = list != null && list.size() != 0 ? list.get(0) : null;
            pairs[i++] = new String[]{key, value};
        }
        return pairs;
    }
}
