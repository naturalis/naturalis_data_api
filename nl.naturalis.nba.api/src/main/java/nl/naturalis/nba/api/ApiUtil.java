package nl.naturalis.nba.api;

import java.util.Arrays;
import java.util.List;

class ApiUtil {

	/**
	 * Null-safe comparison of two List instances with slightly modified
	 * semantics: an empty list and {@code null} are considered equal.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	static <T> boolean equals(List<T> a, List<T> b)
	{
		if (a == null || a.size() == 0) {
			return (b == null || b.size() == 0);
		}
		if (b == null || b.size() == 0) {
			return false;
		}
		return a.equals(b);
	}

	/**
	 * Null-safe comparison of two arrays with slightly modified semantics: a
	 * zero-length array and {@code null} are considered equal.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	static <T> boolean equals(T[] a, T[] b)
	{
		if (a == null || a.length == 0) {
			return (b == null || b.length == 0);
		}
		if (b == null || b.length == 0) {
			return false;
		}
		return Arrays.deepEquals(a, b);
	}

	/**
	 * Null-safe comparison of two objects with slightly modified semantics: a
	 * null value must be treated as being equals to the default value.
	 * 
	 * @param a
	 * @param b
	 * @param dfault
	 * @return
	 */
	static <T> boolean equals(T a, T b, T dfault)
	{
		if (a == null || a.equals(dfault)) {
			return (b == null || b.equals(dfault));
		}
		if (b == null || b.equals(dfault)) {
			return false;
		}
		return a.equals(b);
	}

	static <T> int hashCode(List<T> a)
	{
		return (a == null || a.size() == 0) ? 0 : a.hashCode();
	}

	static <T> int hashCode(T[] a)
	{
		return (a == null || a.length == 0) ? 0 : Arrays.deepHashCode(a);
	}

	static <T> int hashCode(T obj, T dfault)
	{
		return (obj == null || obj.equals(dfault)) ? dfault.hashCode() : obj.hashCode();
	}

	private ApiUtil()
	{
	}

}
