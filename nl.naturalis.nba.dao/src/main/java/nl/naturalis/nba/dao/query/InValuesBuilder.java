package nl.naturalis.nba.dao.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.naturalis.nba.api.QueryCondition;

/**
 * Converts a condition's {@link QueryCondition#getValue() value} to a {@link List}
 * of nun-null values.
 * 
 * @author Ayco Holleman
 *
 */
class InValuesBuilder {

	private List<Object> values;
	private boolean containsNull;

	/**
	 * Create a new {@link InValuesBuilder} for the specified value, supposedly
	 * coming from {@link QueryCondition#getValue()}.
	 * 
	 * @param value
	 */
	InValuesBuilder(Object value)
	{
		if (value == null) {
			values = new ArrayList<>(0);
			containsNull = true;
		}
		else if (value instanceof Collection) {
			handleCollection((Collection<?>) value);
		}
		else if (value.getClass().isArray()) {
			handleArray((Object[]) value);
		}
		else {
			values = Arrays.asList(value);
		}
	}

	/**
	 * Returns a list of non-null values, extracted from a condition's
	 * {@link QueryCondition#getValue() value}.
	 * 
	 * @return
	 */
	List<Object> getValues()
	{
		return values;
	}

	/**
	 * Whether or not a condition's {@link QueryCondition#getValue() value} <i>is</i>
	 * or <i>contains</i> a null value.
	 * 
	 * @return
	 */
	boolean containsNull()
	{
		return containsNull;
	}

	private void handleCollection(Collection<?> objs)
	{
		values = new ArrayList<>(objs.size());
		for (Object obj : objs) {
			if (obj == null)
				containsNull = true;
			else
				values.add(obj);
		}
	}

	private void handleArray(Object[] objs)
	{
		values = new ArrayList<>(objs.length);
		for (Object obj : objs) {
			if (obj == null)
				containsNull = true;
			else
				values.add(obj);
		}
	}

}
