package nl.naturalis.nba.common.es.map;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

import nl.naturalis.nba.api.annotations.Mapped;

class MappingUtil {

	private MappingUtil()
	{
	}

	/**
	 * Returns the type argument for a generic type (e.g. Person for
	 * List&lt;Person&gt;)
	 */
	static Class<?> getClassForTypeArgument(Type t)
	{
		String s = t.toString();
		int i = s.indexOf('<');
		s = s.substring(i + 1, s.length() - 1);
		try {
			return Class.forName(s);
		}
		catch (ClassNotFoundException e) {
			throw new MappingException(e);
		}
	}

	/**
	 * Returns all getter methods of the specified class and its superclasses
	 * (not including {@link Object}) that have the {@link Mapped}
	 * annotation.
	 * 
	 * @param cls
	 * @return
	 */
	static ArrayList<Method> getMappedProperties(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		do {
			hierarchy.add(cls);
			cls = cls.getSuperclass();
		} while (cls != Object.class);
		ArrayList<Method> props = new ArrayList<>(4);
		for (int i = hierarchy.size() - 1; i >= 0; i--) {
			cls = hierarchy.get(i);
			Method[] methods = cls.getDeclaredMethods();
			for (Method m : methods) {
				if (isMappedProperty(m)) {
					props.add(m);
				}
			}
		}
		return props;
	}

	/**
	 * Returns all non-static fields of the specified class and its superclasses
	 * (not including {@link Object}).
	 * 
	 * @param cls
	 * @return
	 */
	static ArrayList<Field> getFields(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		do {
			hierarchy.add(cls);
			cls = cls.getSuperclass();
		} while (cls != Object.class);
		ArrayList<Field> allFields = new ArrayList<>();
		for (int i = hierarchy.size() - 1; i >= 0; i--) {
			cls = hierarchy.get(i);
			Field[] fields = cls.getDeclaredFields();
			for (Field f : fields) {
				if (Modifier.isStatic(f.getModifiers()))
					continue;
				allFields.add(f);
			}
		}
		return allFields;
	}

	/**
	 * Checks whether a getter method is annotated with the
	 * {@link Mapped} annotation.
	 */
	private static boolean isMappedProperty(Method m)
	{
		if (Modifier.isStatic(m.getModifiers()))
			return false;
		if (m.getParameters().length != 0)
			return false;
		Class<?> returnType = m.getReturnType();
		if (returnType == void.class)
			return false;
		String name = m.getName();
		if (name.startsWith("get") && (name.charAt(3) == '_' || isUpperCase(name.charAt(3)))) {
			return null != m.getAnnotation(Mapped.class);
		}
		if (name.startsWith("is") && (name.charAt(2) == '_' || isUpperCase(name.charAt(2)))) {
			if (returnType == boolean.class || returnType == Boolean.class) {
				return null != m.getAnnotation(Mapped.class);
			}
		}
		return false;
	}

	/**
	 * Chops off the first two or three characters of a getter method name
	 * (depending on whether it starts with "is" or "get"), makes the first of
	 * the remaining characters lowercase, and returns the result.
	 * 
	 * @param getter
	 * @return
	 */
	static String extractFieldFromGetter(String getter)
	{
		if (getter.startsWith("get")) {
			return toLowerCase(getter.charAt(3)) + getter.substring(4);
		}
		return toLowerCase(getter.charAt(2)) + getter.substring(3);
	}

}
