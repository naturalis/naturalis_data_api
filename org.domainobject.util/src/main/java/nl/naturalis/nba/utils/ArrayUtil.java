package nl.naturalis.nba.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import nl.naturalis.nba.utils.convert.Converter;
import nl.naturalis.nba.utils.convert.RawConverter;
import nl.naturalis.nba.utils.convert.Stringifier;
import nl.naturalis.nba.utils.convert.Translator;

/**
 * Array utilities.
 * 
 * @author Ayco Holleman
 *
 */
public class ArrayUtil {

	private ArrayUtil()
	{
	}

	/**
	 * Convert an {@code Enumeration} to an array.
	 * 
	 * @param e
	 *            The {@code Enumeration}
	 * @return The array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Enumeration<T> e)
	{
		List<T> list = new ArrayList<>();
		while (e.hasMoreElements()) {
			list.add(e.nextElement());
		}
		return (T[]) list.toArray(new Object[list.size()]);
	}

	/**
	 * Converts the specified array of objects to an array of strings by calling
	 * {@code toString()} on each of its elements.
	 * 
	 * @param array
	 *            The array of objects to stringify
	 * 
	 * @return A String array containing the stringifications
	 */
	public static <T> String[] stringify(T[] array)
	{
		return stringify(array, new Stringifier<T>() {

			public String execute(T obj, Object... args)
			{
				return obj.toString();
			}
		});
	}

	/**
	 * Converts the specified array of objects to an array of strings using the
	 * specified {@link Stringifier}.
	 * 
	 * @param array
	 *            The array of objects to stringify
	 * @param stringifier
	 *            The Stringifier to be used
	 * @param options
	 *            Optional extra arguments, passed on to the {@link Stringifier}
	 *            's {@code execute} method.
	 * 
	 * @return A String array containing the stringifications
	 */
	public static <T> String[] stringify(T[] array, Stringifier<T> stringifier, Object... options)
	{
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; ++i)
			result[i] = stringifier.execute(array[i], options);
		return result;
	}

	/**
	 * Translate all strings in the specified string array using the specified
	 * {@link Translator}. The translations are returned in a new array.
	 * 
	 * @param array
	 *            The {@code String}s to translate
	 * @param translator
	 *            The Translator to be used
	 * @return A new {@code String} array containing the translations
	 */
	public static String[] translate(String[] array, Translator translator)
	{
		return translate(array, translator, false);
	}

	/**
	 * Translate all strings in the specified string array using the specified
	 * {@link Translator}.
	 * 
	 * @param strings
	 *            The array of Strings to convert
	 * @param translator
	 *            The Translator used to manipulate or replace the Strings
	 * @param overwrite
	 *            Whether to overwrite the values in the input array, or create
	 *            a new array
	 * @param options
	 *            Optional extra arguments, passed on to
	 *            {@link Translator#execute(String, Object...)}.
	 * 
	 * @return The original array with the translated {@code String}s
	 */
	public static String[] translate(String[] strings, Translator translator, boolean overwrite,
			Object... options)
	{
		String[] result = overwrite ? strings : new String[strings.length];
		for (int i = 0; i < strings.length; ++i)
			result[i] = translator.execute(strings[i], options);
		return result;
	}

	/**
	 * Converts all objects in the specified array using the specified
	 * converter. The converted objects are returned in a new array.
	 * 
	 * @param array
	 *            The objects to convert
	 * @param converter
	 *            The {@code Converter} to be used
	 * @return A new array containing the converted objects
	 */
	public static Object[] convert(Object[] array, RawConverter converter)
	{
		return convert(array, converter, false, new Object[0]);
	}

	/**
	 * Converts all objects in the specified array using the specified
	 * converter.
	 * 
	 * @param objects
	 *            The objects to be converted
	 * @param converter
	 *            The {@code GenericConverter} to be used
	 * @param overwrite
	 *            Whether to overwrite the values in the input array, or create
	 *            a new array
	 * @param options
	 *            Extra arguments, passed on the
	 *            {@link RawConverter#execute(Object, Object...)}
	 * 
	 * @return The converted objects
	 */
	public static Object[] convert(Object[] objects, RawConverter converter, boolean overwrite,
			Object... options)
	{
		Object[] result = overwrite ? objects : new Object[objects.length];
		for (int i = 0; i < objects.length; ++i)
			result[i] = converter.execute(objects[i], options);
		return result;
	}

	/**
	 * Does a type-save conversion from one type of objects to another type of
	 * objects. The converted objects are returned in a new array.
	 * 
	 * @param objects
	 *            The objects to be converted
	 * @param converter
	 *            The {@code Converter} to be used
	 * @param options
	 *            Extra arguments, passed on to
	 *            {@link Converter#execute(Object, Object...)}.
	 * 
	 * @return The converted objects
	 */
	public static <T, U> U[] convert(T[] objects, Converter<T, U> converter, Object... options)
	{
		@SuppressWarnings("unchecked")
		U[] result = (U[]) new Object[objects.length];
		for (int i = 0; i < objects.length; ++i)
			result[i] = converter.execute(objects[i], options);
		return result;
	}

	/**
	 * Concatenate the specified string arrays.
	 * 
	 * @param arrays
	 *            The String arrays to concatenate
	 * 
	 * @return An array containing all strings in the specified arrays
	 */
	public static String[] concat(String[]... arrays)
	{
		int x = 0;
		for (int i = 0; i < arrays.length; ++i)
			x += arrays[i].length;
		String[] result = new String[x];
		x = 0;
		for (int i = 0; i < arrays.length; ++i) {
			System.arraycopy(arrays[i], 0, result, x, arrays[i].length);
			x += arrays[i].length;
		}
		return result;
	}

	/**
	 * Concatenate the specified arrays.
	 * 
	 * @param arrays
	 *            The arrays to concatenate
	 * 
	 * @return An array containing all objects in the specified arrays
	 */
	public static Object[] concat(Object[]... arrays)
	{
		int x = 0;
		for (int i = 0; i < arrays.length; ++i)
			x += arrays[i].length;
		Object[] result = new Object[x];
		x = 0;
		for (int i = 0; i < arrays.length; ++i) {
			System.arraycopy(arrays[i], 0, result, x, arrays[i].length);
			x += arrays[i].length;
		}
		return result;
	}

	/**
	 * Whether or not the specified object is in the specified array. This
	 * method <i>only</i> compares object references (using the == operator) to
	 * determine the outcome.
	 * 
	 * @param obj
	 *            The object
	 * @param objs
	 *            The array
	 * 
	 * @return Whether or not the array contains a reference to the object
	 * 
	 * @see #has(Object, Object...)
	 */
	/*
	 * Do not change to non-generic form, even though only comparing references,
	 * otherwise clients will have to make unnecessary casts (or get a compiler
	 * warning).
	 */
	@SafeVarargs
	public static <T> boolean in(T obj, T... objs)
	{
		for (T o : objs) {
			if (obj == o)
				return true;
		}
		return false;
	}

	/**
	 * Whether or not the specified character is in the specified array of
	 * characters.
	 * 
	 * @param c
	 *            The character The character to search for
	 * @param chars
	 *            The array of characters to search in
	 * 
	 * @return Whether or not the specified character is in the specified array
	 *         of characters
	 */
	public static boolean in(char c, char... chars)
	{
		for (char ch : chars) {
			if (ch == c)
				return true;
		}
		return false;
	}

	/**
	 * Whether or not the specified object is in the specified array. This
	 * method <i>does</i> use the equals() method to test for the presence of
	 * the object.
	 * 
	 * @param obj
	 *            The object
	 * @param objs
	 *            The array
	 * 
	 * @return Whether or not the specified object is in the specified array.
	 * 
	 * @see #in(Object, Object...)
	 * 
	 */
	@SafeVarargs
	public static <T> boolean has(T obj, T... objs)
	{
		if (obj == null)
			return in(obj, objs);
		for (T o : objs) {
			if (o == null)
				return false;
			if (o.equals(obj))
				return true;
		}
		return false;
	}

	/**
	 * Implodes the specified array using a comma separator. The array elements
	 * are stringified using their {@code toString()} method.
	 * 
	 * @param objects
	 *            The array to implode
	 * 
	 * @return The imploded array
	 */
	public static <T> String implode(T[] objects)
	{
		return implode(objects, ",", new Stringifier<T>() {

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
	 * @param objects
	 *            The array to implode
	 * @param separator
	 *            The separator
	 * 
	 * @return The imploded array
	 */
	public static <T> String implode(T[] objects, String separator)
	{
		return implode(objects, separator, new Stringifier<T>() {

			public String execute(T obj, Object... args)
			{
				return obj.toString();
			}
		});
	}

	/**
	 * Implodes the specified array using the specified {@link Stringifier} and
	 * using a comma separator. If the array is null or empty, an empty String (
	 * {@link StringUtil#E}) is returned.
	 * 
	 * @param objects
	 *            The array to implode
	 * @param callback
	 *            The {@code Stringifier} to be used for each of the array's
	 *            elements.
	 * @param separator
	 *            The separator between the stringified elements
	 * @param options
	 *            Extra arguments, passed on to
	 *            {@link Stringifier#execute(Object, Object...)}
	 * 
	 * @return The imploded array
	 */
	public static <T> String implode(T[] objects, Stringifier<T> callback, Object... options)
	{
		return implode(objects, ",", callback, options);
	}

	/**
	 * Implodes the specified array using the specified separator and
	 * {@link Stringifier}. If the array is null or empty, an empty String (
	 * {@link StringUtil#E}) is returned.
	 * 
	 * @param objects
	 *            The array to implode
	 * @param callback
	 *            The {@code Stringifier} to be used for each of the array's
	 *            elements.
	 * @param separator
	 *            The separator between the stringified elements
	 * @param options
	 *            Extra arguments, passed on to
	 *            {@link Stringifier#execute(Object, Object...)}
	 * 
	 * @return The imploded array
	 */
	public static <T> String implode(T[] objects, String separator, Stringifier<T> callback,
			Object... options)
	{
		if (objects == null || objects.length == 0)
			return StringUtil.EMPTY;
		StringBuilder sb = new StringBuilder(32);
		sb.append(callback.execute(objects[0], options));
		for (int i = 1; i < objects.length; ++i)
			sb.append(separator).append(callback.execute(objects[i], options));
		return sb.toString();
	}

	public static boolean deepEquals(byte[] array1, byte[] array2)
	{
		if (array1.length != array2.length) {
			return false;
		}
		for (int i = 0; i < array1.length; i++) {
			if (array1[i] != array2[i]) {
				return false;
			}
		}
		return true;
	}

}
