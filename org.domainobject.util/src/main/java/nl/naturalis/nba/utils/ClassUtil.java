package nl.naturalis.nba.utils;

import java.math.BigDecimal;

public class ClassUtil {

	private ClassUtil()
	{
	}

	//@formatter:off
	private static final Class<?>[] numberPrimitives = {
		int.class,
		double.class,
		long.class,
		float.class,
		byte.class,
		short.class
	};
	
	@SuppressWarnings("unused")
	private static final Class<?>[] integralPrimitives = {
		int.class,
		long.class,
		byte.class,
		short.class
	};
	
	@SuppressWarnings("unused")
	private static final Class<?>[] integralTypes = {
		int.class,
		Integer.class,
		long.class,
		Long.class,
		byte.class,
		Byte.class,
		short.class,
		Short.class
	};
	
	private static final Class<?>[] wrapperTypes = {
		Integer.class,
		Double.class,
		Long.class,
		Float.class,
		Boolean.class,
		Character.class,
		Byte.class,
		Short.class
	};
	//@formatter:on

	/**
	 * Whether or not the 1st class is, or extends, or implements of the 2nd
	 * class. In other words, whether an instance of the 1st class can be
	 * assigned to a variable of the 2nd class.
	 * 
	 * @param what
	 *            The Class of the value of want to assign (the right-hand side
	 *            of an assignment).
	 * @param interfaceOrSuper
	 *            The class of the variable you want to assign the value to (the
	 *            left-hand side of an assignment).
	 * 
	 * @return Whether or not the 1st class is, or extends, or implements of the
	 *         2nd class.
	 */
	public static boolean isA(Class<?> what, Class<?> interfaceOrSuper)
	{
		return interfaceOrSuper.isAssignableFrom(what);
	}

	/**
	 * Whether or not the 1st argument is an instance of the 2nd argument.
	 * 
	 * @param what
	 *            The value to want to assign (the right-hand side of an
	 *            assignment).
	 * @param interfaceOrSuper
	 *            The class of the variable you want to assign the value to (the
	 *            left-hand side of an assignment).
	 * 
	 * @return Whether or not you can assign "what" to a variable of type
	 *         "target".
	 */
	public static boolean isA(Object what, Class<?> interfaceOrSuper)
	{
		return interfaceOrSuper.isInstance(what);
	}

	/**
	 * Whether or not the specified object is a primitive number. Of course, an
	 * object never is a primitive, unless you got it from Field.get() or
	 * Method.invoke().
	 * 
	 * @param obj
	 *            The object to test
	 * @return Whether or not the specified object is a primitive number.
	 */
	public static boolean isPrimitiveNumber(Object obj)
	{
		return isPrimitiveNumber(obj.getClass());
	}

	/**
	 * Whether or not the specified {@code Class} is one of the primitive number
	 * types (int.class, float.class, etc.)
	 * 
	 * @param c
	 *            The {@code Class} to test The class you are interested in
	 * @return
	 */
	public static boolean isPrimitiveNumber(Class<?> c)
	{
		return ArrayUtil.in(c, numberPrimitives);
	}

	/**
	 * Whether or not the specified object is a primitive number or a
	 * {@link Number}.
	 * 
	 * @param obj
	 *            The object to test
	 * @return Whether or not the specified {@code Class} is one of the
	 *         primitive number classes
	 */
	public static boolean isNumber(Object obj)
	{
		return isNumber(obj.getClass());
	}

	/**
	 * Whether or not the specified class is one of the primitive number types
	 * or a {@link Number}.
	 * 
	 * @param c
	 *            The {@code Class} to test
	 * @return
	 */
	public static boolean isNumber(Class<?> c)
	{
		return isPrimitiveNumber(c) || isA(c, Number.class);
	}

	/**
	 * Whether or not the specified object is a number, or a character or string
	 * representing a number.
	 * 
	 * @param obj
	 *            The object to test
	 * @return Whether the object can be represented as a number.
	 */
	public static boolean isNumeric(Object obj)
	{
		if (isNumber(obj))
			return true;
		if (obj.getClass() == char.class || obj.getClass() == Character.class) {
			char c = ((Character) obj).charValue();
			if (c >= '0' && c <= '9')
				return true;
		}
		else if (obj instanceof CharSequence) {
			try {
				new BigDecimal(obj.toString());
				return true;
			}
			catch (NumberFormatException e) {}
		}
		return false;
	}

	public static boolean isWrapper(Class<?> cls)
	{
		return ArrayUtil.in(cls, wrapperTypes);
	}

}
