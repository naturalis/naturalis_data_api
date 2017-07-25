package nl.naturalis.nba.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

class FieldCache {

	private static final HashMap<Class<?>, HashMap<String, Field>> cache = new HashMap<>();

	static Field get(String name, Class<?> cls) throws InvalidPathException
	{
		HashMap<String, Field> subcache = cache.get(cls);
		if (subcache == null) {
			subcache = new HashMap<>();
			cache.put(cls, subcache);
		}
		Field f = subcache.get(name);
		if (f == null) {
			f = getField(name, cls);
			if (f == null) {
				String msg = String.format("Invalid path element: \"%s\"", name);
				throw new InvalidPathException(msg);
			}
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			subcache.put(name, f);
		}
		return f;
	}

	private static Field getField(String name, Class<?> cls)
	{
		while (cls != Object.class) {
			for (Field f : cls.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers()))
					continue;
				if (f.getName().equals(name))
					return f;
			}
			cls = cls.getSuperclass();
		}
		return null;
	}

}
