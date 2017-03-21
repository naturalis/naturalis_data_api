package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;

import nl.naturalis.nba.api.Path;

public class PathValueComparator<T> implements Comparator<T> {

	private Path path;

	public PathValueComparator(Path path)
	{
		this.path = path;
	}

	@Override
	public int compare(T o1, T o2)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	private int compare(Path path, Object o1, Object o2)
	{
		Class<?> cls = o1.getClass();
		return 0;
	}

	private static ArrayList<Field> getFields(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		Class<?> c = cls;
		while (c != Object.class) {
			hierarchy.add(c);
			c = c.getSuperclass();
		}
		ArrayList<Field> allFields = new ArrayList<>();
		for (int i = hierarchy.size() - 1; i >= 0; i--) {
			c = hierarchy.get(i);
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				if (Modifier.isStatic(f.getModifiers()))
					continue;
				if(!f.isAccessible()) {
					//f.s
				}
				allFields.add(f);
			}
		}
		return allFields;
	}
}
