package nl.naturalis.nba.dao;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.dao.util.es.SearchHitHandler;

class DistinctValuesPerGroupSearchHitHandler implements SearchHitHandler {

	private final Map<Object, Set<Object>> result = new TreeMap<>();

	private final String keyField;
	private final String valuesField;

	DistinctValuesPerGroupSearchHitHandler(String keyField, String valuesField)
	{
		if (keyField.equals(valuesField)) {
			throw new IllegalArgumentException("Key field must not be equal to values field");
		}
		this.keyField = keyField;
		this.valuesField = valuesField;
	}

	@Override
	public void handle(SearchHit hit) throws InvalidQueryException
	{
		Map<String, Object> source = hit.getSource();
		Object key = source.get(keyField);
		if (key == null) {
			return;
		}
		Object value = source.get(valuesField);
		Set<Object> values = result.get(key);
		if (values == null) {
			if (result.size() == 100) {
				String fmt = "Number of unique values for field (%s) exceeds maximum of 100";
				String msg = String.format(fmt, keyField);
				throw new InvalidQueryException(msg);
			}
			values = new TreeSet<>();
			result.put(key, values);
		}
		if (value instanceof Collection) {
			values.addAll((Collection<?>) value);
			if (values.size() > 1000) {
				String fmt = "Number of unique values for field (%s) exceeds maximum of 1000";
				String msg = String.format(fmt, valuesField);
				throw new InvalidQueryException(msg);
			}
		}
		else if (value != null) {
			values.add(value);
		}
	}

	Map<Object, Set<Object>> getDistinctValuesPerGroup()
	{
		return result;
	}

}
