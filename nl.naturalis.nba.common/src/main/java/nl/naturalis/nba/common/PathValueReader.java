package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.utils.ClassUtil;

/**
 * A {@code PathReader} reads and returns the value of a field within an object.
 * The field is specified by means of a {@link Path}. The value is always
 * returned as a {@link List}, even if all path elements are single-valued
 * objects (i.e. they are neither arrays nor {@link Collection} objects). Also
 * note that more than one path element may be multi-valued. In that case,
 * <i>all</i> values for the last path element are collected and returned in a
 * new {@code List}.
 * 
 * @author Ayco Holleman
 *
 */
public class PathValueReader {

	private Path path;

	public PathValueReader(Path path)
	{
		this.path = path;
	}

	public List<Object> readValue(Object obj) throws InvalidPathException
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
			values.add(child);
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
