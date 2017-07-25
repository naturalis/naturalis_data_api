package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import nl.naturalis.nba.api.Path;

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

	public PathValueReader(String path)
	{
		this(new Path(path));
	}

	public PathValueReader(Path path)
	{
		this.path = path;
	}

	public Object read(Object obj) throws InvalidPathException
	{
		Objects.requireNonNull(obj, "Object must not be null");
		if (obj instanceof Iterable) {
			throw new IllegalArgumentException("Object must not be an Iterable");
		}
		if (path.countElements() == 0) {
			return obj;
		}
		for (int i = 0; i < path.countElements(); i++) {
			obj = readField(path.getElement(i), obj);
			if (obj == null || i == path.countElements() - 1) {
				return obj;
			}
			if (obj instanceof Iterable) {
				try {
					int idx = Integer.parseInt(path.getElement(i + 1));
					Iterator<?> iterator = ((Iterable<?>) obj).iterator();
					for (int j = 0; j < idx; j++) {
						if (!iterator.hasNext()) {
							return null;
						}
						obj = iterator.next();
					}
					if (obj == null || ++i == path.countElements() - 1) {
						return obj;
					}
				}
				catch (NumberFormatException e) {
					String fmt = "Missing array index after %s in path %s";
					String msg = String.format(fmt, path.getElement(i), path);
					throw new InvalidPathException(msg);
				}
			}
		}
		/* Won't get here */ return null;
	}

	private static Object readField(String field, Object obj) throws InvalidPathException
	{
		Field f = FieldCache.get(field, obj.getClass());
		try {
			return f.get(obj);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
