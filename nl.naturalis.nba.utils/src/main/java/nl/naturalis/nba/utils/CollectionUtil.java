package nl.naturalis.nba.utils;

import static nl.naturalis.nba.utils.convert.Stringifier.BASIC_STRINGIFIER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.naturalis.nba.utils.convert.Stringifier;

public class CollectionUtil {

	public static <T> List<String> stringify(Collection<T> collection, Stringifier<T> stringifier,
			Object... options)
	{
		List<String> result = new ArrayList<>(collection.size());
		for (T obj : collection) {
			result.add(stringifier.execute(obj, options));
		}
		return result;
	}

	/**
	 * Implodes the specified collection using a comma separator. The array
	 * elements are stringified using their {@code toString()} method.
	 * 
	 * @param collection
	 *            The collection to implode
	 * 
	 * @return The imploded collection
	 */
	public static <T> String implode(Collection<T> collection)
	{
		return implode(collection, ",", new Stringifier<T>() {

			public String execute(T obj, Object... args)
			{
				return obj.toString();
			}
		});
	}

	/**
	 * Implodes the specified array using the specified separator. The array
	 * elements are stringified using their {@code toString()} method.
	 * 
	 * @param collection
	 *            The collection to implode
	 * @param separator
	 *            The separator
	 * 
	 * @return The imploded collection
	 */
	public static <T> String implode(Collection<T> collection, String separator)
	{
		return implode(collection, separator, new Stringifier<T>() {

			public String execute(T obj, Object... args)
			{
				return obj.toString();
			}
		});
	}

	/**
	 * Implodes the specified collection using the specified {@link Stringifier}
	 * and using a comma separator. If the array is null or empty, an empty
	 * String ( {@link StringUtil#E}) is returned.
	 * 
	 * @param collection
	 *            The collection to implode
	 * @param stringifier
	 *            The {@code Stringifier} to be used for each of the array's
	 *            elements.
	 * @param separator
	 *            The separator between the stringified elements
	 * @param options
	 *            Extra arguments, passed on to
	 *            {@link Stringifier#execute(Object, Object...)}
	 * 
	 * @return The imploded collection
	 */
	public static <T> String implode(Collection<T> collection, Stringifier<T> stringifier,
			Object... options)
	{
		return implode(collection, ",", stringifier, options);
	}

	/**
	 * Implodes the specified collection using the specified separator and
	 * {@link Stringifier}. If the array is null or empty, an empty String (
	 * {@link StringUtil#E}) is returned.
	 * 
	 * @param objects
	 *            The collection to implode
	 * @param stringifier
	 *            The {@code Stringifier} to be used for each of the array's
	 *            elements.
	 * @param sep
	 *            The separator between the stringified elements
	 * @param options
	 *            Extra arguments, passed on to
	 *            {@link Stringifier#execute(Object, Object...)}
	 * 
	 * @return The imploded collection
	 */
	public static <T> String implode(Collection<T> collection, String sep,
			Stringifier<T> stringifier, Object... options)
	{
		if (collection == null || collection.size() == 0)
			return StringUtil.EMPTY;
		StringBuilder sb = new StringBuilder(32);
		boolean first = true;
		for (T obj : collection) {
			if (first)
				first = false;
			else
				sb.append(sep);
			sb.append(stringifier.execute(obj, options));
		}
		return sb.toString();
	}

}
