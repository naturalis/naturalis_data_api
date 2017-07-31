package nl.naturalis.nba.utils;

public class ObjectUtil {

	/**
	 * Null-tolerant comparison of two objects.
	 * 
	 * @param obj0
	 * @param obj1
	 * @return
	 */
	public static <T extends Comparable<T>> int compare(T obj0, T obj1)
	{
		if (obj0 == null) {
			if (obj1 == null) {
				return 0;
			}
			return 1;
		}
		if (obj1 == null) {
			return -1;
		}
		return obj0.compareTo(obj1);
	}

	private ObjectUtil()
	{
	}

}
