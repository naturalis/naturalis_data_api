package nl.naturalis.nba.api;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	 * null value must be treated as being equal to the default value for the
	 * type of objects being compared.
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

	/**
	 * Null-safe hash code calculation for lists with slightly modified
	 * semantics: a null value must be treated as being equal to an empty list
	 * 
	 * @param a
	 * @return
	 */
	static <T> int hashCode(List<T> a)
	{
		return (a == null || a.size() == 0) ? 0 : a.hashCode();
	}

	/**
	 * Null-safe hash code calculation for arrays with slightly modified
	 * semantics: a null value must be treated as being equal to an empty array
	 * 
	 * @param a
	 * @return
	 */
	static <T> int hashCode(T[] a)
	{
		return (a == null || a.length == 0) ? 0 : Arrays.deepHashCode(a);
	}

	/**
	 * Null-safe hash code calculation for arrays with slightly modified
	 * semantics: a null value must be treated as being equal to the default
	 * value for the type of objects being compared.
	 * 
	 * 
	 * @param a
	 * @return
	 */
	static <T> int hashCode(T obj, T dfault)
	{
		return (obj == null) ? dfault.hashCode() : obj.hashCode();
	}

	/**
	 * Null-safe hash code objects with <i>regular</i> semantics. This method
	 * must be seen as the counterpart of the
	 * {@link Objects#deepEquals(Object, Object) deepEquals} method in the
	 * {@link Objects} class. Strangely, that class does not itself provide this
	 * counterpart. If the argument passed to {@code deepHashCode} is
	 * {@code null}, 0 is returned. If the argument is an array of some
	 * primitive type, then {@code Arrays.hashCode} is called. If the argument
	 * is an array of some non-primitive type, then
	 * {@link Arrays#deepHashCode(Object[]) Arrays#deepHashCode} is called.
	 * Otherwise the {@code hashCode()} method of the argument is called.
	 * 
	 * @param obj
	 * @return
	 */
	static <T> int deepHashCode(T obj)
	{
		if (obj == null) {
			return 0;
		}
		if (obj.getClass() == int[].class) {
			return Arrays.hashCode((int[]) obj);
		}
		if (obj.getClass() == double[].class) {
			return Arrays.hashCode((double[]) obj);
		}
		if (obj.getClass() == char[].class) {
			return Arrays.hashCode((char[]) obj);
		}
		if (obj.getClass() == long[].class) {
			return Arrays.hashCode((long[]) obj);
		}
		if (obj.getClass() == float[].class) {
			return Arrays.hashCode((float[]) obj);
		}
		if (obj.getClass() == boolean[].class) {
			return Arrays.hashCode((boolean[]) obj);
		}
		if (obj.getClass() == byte[].class) {
			return Arrays.hashCode((byte[]) obj);
		}
		if (obj.getClass() == short[].class) {
			return Arrays.hashCode((short[]) obj);
		}
		if (obj instanceof Object[]) {
			return Arrays.deepHashCode((Object[]) obj);
		}
		return obj.hashCode();
	}

	private ApiUtil()
	{
	}

}
