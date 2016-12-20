package nl.naturalis.nba.common.es.map;

import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.utils.ClassUtil;

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
	 * (not including {@link Object}) that are to be mapped to the document
	 * store.
	 * 
	 * @param cls
	 * @return
	 */
	static ArrayList<Method> getMappedProperties(Class<?> cls)
	{
		ArrayList<Class<?>> hierarchy = new ArrayList<>(3);
		while (cls != Object.class) {
			hierarchy.add(cls);
			cls = cls.getSuperclass();
		}
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
				/*
				 * Do not map the "id" field of the IDocumentObject class
				 * itself. This field is populated with the system ID of the
				 * Elasticsearch document, which is not part of the document
				 * source. Note though that we cannot @JsonIgnore this field
				 * because we do want it serialized.
				 */
				if (ClassUtil.isA(c, IDocumentObject.class) && f.getName().equals("id"))
					continue;
				if (f.getAnnotation(JsonIgnore.class) != null)
					continue;
				allFields.add(f);
			}
		}
		return allFields;
	}

	/**
	 * Checks whether the method is a getter method and is annotated with
	 * {@link JsonProperty}.
	 */
	private static boolean isMappedProperty(Method m)
	{
		if (Modifier.isStatic(m.getModifiers()))
			return false;
		if (m.getParameters().length != 0)
			return false;
		Class<?> rt = m.getReturnType();
		if (rt == void.class)
			return false;
		if (m.getAnnotation(JsonProperty.class) != null) {
			String name = m.getName();
			if (name.startsWith("get")) {
				return isUpperCase(name.charAt(3));
			}
			if ((rt == boolean.class || rt == Boolean.class) && name.startsWith("is")) {
				return isUpperCase(name.charAt(2));
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
