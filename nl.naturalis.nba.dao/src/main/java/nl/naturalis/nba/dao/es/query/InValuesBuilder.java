package nl.naturalis.nba.dao.es.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.naturalis.nba.api.query.Condition;

/**
 * Converts a condition's {@link Condition#getValue() value} (which can be any
 * type of object) to a list of nun-null values.
 * 
 * @author Ayco Holleman
 *
 */
class InValuesBuilder {

	private List<Object> values;
	private boolean containsNull;

	/**
	 * Create a new {@link InValuesBuilder} for the specified value, supposedly
	 * coming from {@link Condition#getValue()}.
	 * 
	 * @param value
	 */
	public InValuesBuilder(Object value)
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
	 * {@link Condition#getValue() value}.
	 * 
	 * @return
	 */
	public List<Object> getValues()
	{
		return values;
	}

	/**
	 * Whether or not a condition's {@link Condition#getValue() value} <i>is</i>
	 * or <i>contains</i> a null value.
	 * 
	 * @return
	 */
	public boolean containsNull()
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
