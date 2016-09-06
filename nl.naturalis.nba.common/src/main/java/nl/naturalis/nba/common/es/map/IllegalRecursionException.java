package nl.naturalis.nba.common.es.map;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Thrown by {@link MappingFactory#getMapping(Class)} for Java classes that
 * cannot be mapped because of circularity in the object graph.
 * 
 * @author Ayco Holleman
 *
 */
public class IllegalRecursionException extends MappingException {

	private static String MSG_PATTERN = "Illegal recursive nesting of type %s in %s %s";

	public IllegalRecursionException(Field field, Class<?> type)
	{
		super(String.format(MSG_PATTERN, type.getName(), "field", field.getName()));
	}

	public IllegalRecursionException(Method method, Class<?> type)
	{
		super(String.format(MSG_PATTERN, type.getName(), "method", method.getName()));
	}

}
