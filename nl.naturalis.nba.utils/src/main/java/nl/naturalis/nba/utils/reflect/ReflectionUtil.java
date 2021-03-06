package nl.naturalis.nba.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Java reflection utility class, useful and convenient for unit testing. Especially targeted at
 * manipulating private class members. May not be optimized enough for production code.
 */
public class ReflectionUtil {

  private ReflectionUtil() {}

  /**
   * Calls the no-arg constructor for the specified class. The constructor may be private.
   * 
   * @param cls The class of which to create a new instance
   * @return
   */
  public static <T> T newInstance(Class<T> cls) {
    return newInstance(cls, new Class[] {}, new Object[] {});
  }

  /**
   * Calls the constructor that takes the specified arguments. The constructor may be private. The
   * types of the arguments you pass via the <code>args</code> array must <i>exactly</i> match the
   * declared method signature. If any of the declared parameters is a primitive type you
   * <i>cannot</i> use this method. You will get a {@link NoSuchMethodException}. You must use
   * {@link #newInstance(Class, Class[], Object...)} instead.
   * 
   * @param cls The class of which to create a new instance
   * @param args The arguments to be passed to the constructor
   * @return
   */
  public static <T> T newInstance(Class<T> cls, Object... args) {
    return newInstance(cls, getParamTypes(args), args);
  }

  /**
   * Calls the constructor takes the specified arguments. The constructor may be private. The types
   * of the constructor arguments are specified explicitly through the <code>paramTypes</code>.
   * parameter.
   * 
   * @param cls The class of which to create a new instance
   * @param paramTypes The types of the constructor arguments
   * @param args The arguments to be passed to the constructor
   * @return
   */
  public static <T> T newInstance(Class<T> cls, Class<?>[] paramTypes, Object... args) {
    try {
      Constructor<T> c = cls.getDeclaredConstructor(paramTypes);
      c.setAccessible(true);
      return c.newInstance(args); 
    } 
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the specified instance field on the specified object to the specified value.
   * 
   * @param obj The object on which to set the value
   * @param field The field whose value to set
   * @param value The value to set
   */
  public static void set(Object obj, String field, Object value) {
    Field f = getField(obj.getClass(), field);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    try {
      f.setAccessible(true);
      f.set(obj, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the specified static field on the specified object to the specified value.
   * 
   * @param obj The class that contains the static field whose value to set
   * @param field The field whose value to set
   * @param value The value to set
   */
  public static void setStatic(Class<?> cls, String field, Object value) {
    Field f = getField(cls, field);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (!Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not a static field: " + field);
    }
    try {
      f.setAccessible(true);
      f.set(null, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the value of the specified instance field in the specified object.
   * 
   * @param obj The object containing the value
   * @param field The field containing the value
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T get(Object obj, String field) {
    Class<?> c = obj.getClass();
    Field f = getField(c, field);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not an instance field: " + field);
    }
    try {
      f.setAccessible(true);
      return (T) f.get(obj);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the value of the specified static field in the specified object.
   * 
   * @param cls The class containing the field whose value to return
   * @param field The field whose value to return
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T getStatic(Class<?> cls, String field) {
    Field f = getField(cls, field);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (!Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not a static field: " + field);
    }
    try {
      f.setAccessible(true);
      return (T) f.get(null);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calls the specified zero-arguments instance method on the specified object.
   * 
   * @param obj The object on which to invoke the method
   * @param method The name of the method to invoke
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T call(Object obj, String method) {
    return (T) call(obj, method, new Class[] {}, new Object[] {});
  }

  /**
   * Calls the specified instance method on the specified object. The types of the arguments you
   * pass via the <code>args</code> array must <i>exactly</i> match the declared method signature.
   * If any of the declared parameters is a primitive type you <i>cannot</i> use this method. You
   * will get a {@link NoSuchMethodException}. Use
   * {@link #call(Object, String, Class, Class[], Object...)} instead.
   * 
   * @param obj The object on which to invoke the method
   * @param method The name of the method to invoke
   * @param args The arguments to be passed to the method
   * @return
   */
  public static <T> T call(Object obj, String method, Object... args) {
    return call(obj, method, getParamTypes(args), args);
  }

  /**
   * Calls the specified instance method on the specified object. The types of the method arguments
   * are specified explicitly through the <code>paramTypes</code> parameter.
   * 
   * @param obj The object on which to invoke the method
   * @param method The name of the method to invoke
   * @param paramTypes The types of the method parameters
   * @param args The arguments to be passed to the method
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T call(Object obj, String method, Class<?>[] paramTypes, Object... args) {
    Method m = getMethod(obj.getClass(), method, paramTypes);
    if (m == null) {
      throw new RuntimeException("No such method: " + method);
    }
    if (Modifier.isStatic(m.getModifiers())) {
      throw new RuntimeException("Not an instance method: " + method);
    }
    try {
      m.setAccessible(true);
      return (T) m.invoke(obj, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calls the specified static, zero-arguments method on the specified class.
   * 
   * @param cls
   * @param method
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T callStatic(Class<?> cls, String method) {
    return (T) callStatic(cls, method, new Class[] {}, new Object[] {});
  }

  /**
   * Calls the specified static method on the specified class. The types of the arguments you pass
   * via the <code>args</code> array must <i>exactly</i> match the declared method signature. If any
   * of the declared parameters is a primitive type you <i>cannot</i> use this method. You will get
   * a {@link NoSuchMethodException}. Use
   * {@link #callStatic(Class, String, Class, Class[], Object...) instead.
   * 
   * @param cls The class containing the static method to invoke
   * @param method The static method to invoke
   * @param args The arguments to be passed to the method
   * @return
   */
  public static <T> T callStatic(Class<?> cls, String method, Object... args) {
    return callStatic(cls, method, getParamTypes(args), args);
  }

  /**
   * Calls the specified static method on the specified class. The types of the method arguments are
   * specified explicitly through the <code>paramTypes</code> parameter.
   * 
   * @param cls
   * @param method
   * @param paramTypes
   * @param args
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> T callStatic(Class<?> cls, String method, Class<?>[] paramTypes,
      Object... args) {
    Method m = getMethod(cls, method, paramTypes);
    if (m == null) {
      throw new RuntimeException("No such method: " + method);
    }
    if (!Modifier.isStatic(m.getModifiers())) {
      throw new RuntimeException("Not a static method: " + method);
    }
    try {
      m.setAccessible(true);
      return (T) m.invoke(null, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the {@link Field} object corresponding to the specified field name. The field must be
   * declared in the specified class or anywhere higher up the class hierarchy. It may be static and
   * it may be private.
   * 
   * @param cls
   * @param name
   * 
   * @return
   */
  public static Field getField(Class<?> cls, String name) {
    while (cls != null) {
      for (Field f : cls.getDeclaredFields()) {
        if (f.getName().equals(name))
          return f;
      }
      cls = cls.getSuperclass();
    }
    return null;
  }

  /**
   * Returns the {@link Method} object corresponding to the specified method name. The method must
   * be declared in the specified class or anywhere higher up the class hierarchy. It may be static
   * and it may be private.
   * 
   * @param cls
   * @param name
   * 
   * @return
   */
  public static Method getMethod(Class<?> cls, String name, Class<?>... parameterTypes) {
    while (cls != null) {
      try {
        return cls.getDeclaredMethod(name, parameterTypes);
      } catch (NoSuchMethodException e) {
        // move to super class
      }
      cls = cls.getSuperclass();
    }
    return null;
  }

  private static Class<?>[] getParamTypes(Object... args) {
    Class<?>[] paramTypes = new Class[args.length];
    for (int i = 0; i < args.length; ++i) {
      paramTypes[i] = args[i].getClass();
    }
    return paramTypes;
  }

}
