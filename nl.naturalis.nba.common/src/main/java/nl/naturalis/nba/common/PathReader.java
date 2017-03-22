package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.utils.ClassUtil;

public class PathReader {

	private Path path;

	public PathReader(Path path)
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
		ArrayList<Object> values = new ArrayList<>();
		read(path, obj, values);
		return values;
	}

	private static void read(Path path, Object obj, ArrayList<Object> values)
			throws InvalidPathException
	{
		if (path.countElements() == 0) {
			return;
		}
		Field f = FieldCache.get(path.getElement(0), obj.getClass());
		if (!f.isAccessible()) {
			f.setAccessible(true);
		}
		Object child = getValue(f, obj);
		if (child != null) {
			if (ClassUtil.isA(f.getType(), Iterable.class)) {
				for (Object elem : (Iterable<?>) child) {
					read(path.shift(), elem, values);
				}
			}
			else {
				if (path.countElements() == 1) {
					values.add(child);
				}
				else {
					read(path.shift(), child, values);
				}
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
