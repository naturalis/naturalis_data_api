package nl.naturalis.nba.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nl.naturalis.nba.utils.debug.BeanPrinter;

public class ReflectionUtil {

  private ReflectionUtil() {}

  /**
   * Creates a new instance of the specified class invoking the constructor taking the specified
   * arguments.
   * 
   * @param cls
   * @param constructorArgs
   * @return
   */
  public static <T> T newInstance(Class<T> cls, Object... constructorArgs) {
    Class<?>[] paramTypes = getParamTypes(constructorArgs);
    try {
      Constructor<T> c = cls.getDeclaredConstructor(paramTypes);
      if (!c.isAccessible()) {
        c.setAccessible(true);
      }
      return c.newInstance(constructorArgs);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the specified instance field on the specified object to the specified value.
   * 
   * @param obj
   * @param field
   * @param value
   */
  public static void set(Object obj, String field, Object value) {
    Field f = getField(field, obj.getClass());
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (!f.isAccessible()) {
      f.setAccessible(true);
    }
    try {
      f.set(obj, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets the specified instance field on the specified object to the specified value.
   * 
   * @param obj
   * @param field
   * @param value
   */
  public static void setStatic(Class<?> cls, String field, Object value) {
    Field f = getField(field, cls);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (!Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not a static field: " + field);
    }
    if (!f.isAccessible()) {
      f.setAccessible(true);
    }
    try {
      f.set(null, value);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the value of the specified instance field in the specified object, casting it to the
   * specified return type.
   * 
   * @param obj
   * @param field
   * @param returnType
   * @return
   */
  public static <T> T get(Object obj, String field, Class<T> returnType) {
    Class<?> c = obj.getClass();
    Field f = getField(field, c);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not an instance field: " + field);
    }
    if (!f.isAccessible()) {
      f.setAccessible(true);
    }
    try {
      return returnType.cast(f.get(obj));
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns the value of the specified static field in the specified object, casting it to the
   * specified return type.
   * 
   * @param obj
   * @param field
   * @param returnType
   * @return
   */
  public static <T> T getStatic(Class<?> cls, String field, Class<T> returnType) {
    Field f = getField(field, cls);
    if (f == null) {
      throw new RuntimeException("No such field: " + field);
    }
    if (!Modifier.isStatic(f.getModifiers())) {
      throw new RuntimeException("Not a static field: " + field);
    }
    if (!f.isAccessible()) {
      f.setAccessible(true);
    }
    try {
      return returnType.cast(f.get(null));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calls the specified instance method on the specified object, casting the return value (if any)
   * to the specified type.
   * 
   * @param obj
   * @param method
   * @param returnType
   * @param args
   * @return
   */
  public static <T> T call(Object obj, String method, Class<T> returnType, Object... args) {
    Class<?>[] paramTypes = getParamTypes(args);
    Method m = getMethod(method, obj.getClass(), int.class, boolean.class);
    if (m == null) {
      throw new RuntimeException("No such method: " + method);
    }
    if (Modifier.isStatic(m.getModifiers())) {
      throw new RuntimeException("Not an instance method: " + method);
    }
    if (!m.isAccessible()) {
      m.setAccessible(true);
    }
    try {
      Object result = m.invoke(obj, args);
      if (result == null || returnType == null || returnType == void.class) {
        return null;
      }
      return returnType.cast(result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Calls the specified static method on the specified object, casting the return value (if any) to
   * the specified type.
   * 
   * @param obj
   * @param method
   * @param returnType
   * @param args
   * @return
   */
  public static <T> T callStatic(Class<?> cls, String method, Class<T> returnType, Object... args) {
    Class<?>[] paramTypes = getParamTypes(args);
    Method m = getMethod(method, cls, paramTypes);
    if (m == null) {
      throw new RuntimeException("No such method: " + method);
    }
    if (!Modifier.isStatic(m.getModifiers())) {
      throw new RuntimeException("Not a static method: " + method);
    }
    if (!m.isAccessible()) {
      m.setAccessible(true);
    }
    try {
      Object result = m.invoke(null, args);
      if (result == null || returnType == null || returnType == void.class) {
        return null;
      }
      return returnType.cast(result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a {@link Field} object corresponding to the specified field name. The field must be
   * declared in the specified class or anywhere higher up the class hierarchy. It may be static and
   * it may be private.
   * 
   * @param name
   * @param cls
   * @return
   */
  public static Field getField(String name, Class<?> cls) {
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
   * Returns a {@link Method} object corresponding to the specified method name. The method must be
   * declared in the specified class or anywhere higher up the class hierarchy. It may be static and
   * it may be private.
   * 
   * @param name
   * @param cls
   * @return
   */
  public static Method getMethod(String name, Class<?> cls, Class<?>... parameterTypes) {
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
