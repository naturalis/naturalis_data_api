package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.utils.ClassUtil;

/*
 * This package-private class is a non-generic version of the PathValueReader
 * class, specifically built for the PathValueComparator. It will exclude null
 * values from the list returned by getValue() since these would generate a
 * NullPointerException when sorting lists or arrays usings Collections.sort().
 * Also this class ensures that the objects added to the list returned by
 * getValue() implement the Comparable interface.
 */
class NullSkippingPathValueReader {

	private Path path;

	NullSkippingPathValueReader(Path path)
	{
		this.path = path;
	}

	List<Object> readValue(Object obj) throws InvalidPathException
	{
		if (obj == null) {
			String fmt = "Object must not be null when reading %s";
			String msg = String.format(fmt, path);
			throw new IllegalArgumentException(msg);
		}
		if (path.countElements() == 0) {
			throw new InvalidPathException("Path must not be null");
		}
		ArrayList<Object> values = new ArrayList<>();
		read(path, obj, values);
		return values;
	}

	private static void read(Path path, Object obj, ArrayList<Object> values)
			throws InvalidPathException
	{
		Field f = FieldCache.get(path.getElement(0), obj.getClass());
		if (!f.isAccessible()) {
			f.setAccessible(true);
		}
		Object child = getValue(f, obj);
		if (path.countElements() == 1) {
			if (child != null) {
				if (!(child instanceof Comparable)) {
					String fmt = "Values of %s cannot be sorted";
					String msg = String.format(fmt, path);
					throw new IllegalArgumentException(msg);
				}
				values.add(child);
			}
		}
		else if (child != null) {
			if (ClassUtil.isA(f.getType(), Iterable.class)) {
				for (Object elem : (Iterable<?>) child) {
					read(path.shift(), elem, values);
				}
			}
			else {
				read(path.shift(), child, values);
			}
		}
	}

	private static Object getValue(Field f, Object obj)
	{
		try {
			return f.get(obj);
		}
		catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
