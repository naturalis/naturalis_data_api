package nl.naturalis.nba.utils;

public class ObjectUtil {

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
