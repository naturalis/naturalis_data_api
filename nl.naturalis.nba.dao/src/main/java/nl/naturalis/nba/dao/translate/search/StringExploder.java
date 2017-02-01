package nl.naturalis.nba.dao.translate.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.naturalis.nba.api.QueryCondition;

/**
 * Converts a condition's {@link QueryCondition#getValue() value} to a
 * {@link List} of nun-null values.
 * 
 * @author Ayco Holleman
 *
 */
class StringExploder {

	private List<String> values;
	private boolean containsNull;
	private boolean containsNonCharSequence;

	/**
	 * Create a new {@link StringExploder} for the specified value, supposedly
	 * coming from {@link QueryCondition#getValue()}.
	 * 
	 * @param value
	 */
	StringExploder(Object value)
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
			if (!(value instanceof CharSequence)) {
				containsNonCharSequence = true;
			}
			values = Arrays.asList(value.toString());
		}
	}

	/**
	 * Returns a list of non-null values, extracted from a condition's
	 * {@link QueryCondition#getValue() value}.
	 * 
	 * @return
	 */
	List<String> getValues()
	{
		return values;
	}

	/**
	 * Whether or not a condition's {@link QueryCondition#getValue() value}
	 * <i>is</i> or <i>contains</i> a null value.
	 * 
	 * @return
	 */
	boolean containsNull()
	{
		return containsNull;
	}

	boolean containsNonCharSequence()
	{
		return containsNonCharSequence;
	}

	private void handleCollection(Collection<?> objs)
	{
		values = new ArrayList<>(objs.size());
		for (Object obj : objs) {
			if (obj == null) {
				containsNull = true;
			}
			else {
				if (!(obj instanceof CharSequence)) {
					containsNonCharSequence = true;
				}
				values.add(obj.toString());
			}
		}
	}

	private void handleArray(Object[] objs)
	{
		values = new ArrayList<>(objs.length);
		for (Object obj : objs) {
			if (obj == null) {
				containsNull = true;
			}
			else {
				if (!(obj instanceof CharSequence)) {
					containsNonCharSequence = true;
				}
				values.add(obj.toString());
			}
		}
	}

}
