package nl.naturalis.nba.utils.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtil {

  private ReflectionUtil() {}

  public static <T> T instantiate(Class<T> cls, Object... constructorArgs) {
    Class<?>[] paramTypes = new Class[constructorArgs.length];
    for (int i = 0; i < constructorArgs.length; ++i) {
      paramTypes[i] = constructorArgs[i].getClass();
    }
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

  public static <T> T get(Object obj, String field, Class<T> returnType) {
    Class<?> c = obj.getClass();
    Field f = getField(field, c);
    if (c == null) {
      throw new RuntimeException("No such field: " + field);
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

  public static <T> T call(Object obj, String method, Class<T> returnType, Object... args) {
    Method m = getMethod(method, obj.getClass());
    if (m == null) {
      throw new RuntimeException("No such method: " + method);
    }
    return null;
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
  public static Method getMethod(String name, Class<?> cls) {
    while (cls != null) {
      for (Method m : cls.getDeclaredMethods()) {
        if (m.getName().equals(name))
          return m;
      }
      cls = cls.getSuperclass();
    }
    return null;
  }

  public static Method getMethod(String name, Class<?> cls, Class<?>... parameterTypes) {
    while (cls != null) {
      try {
        return cls.getDeclaredMethod(name, parameterTypes);
      } catch (SecurityException e) {
        throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
        //
      }
      cls = cls.getSuperclass();
    }
    return null;
  }
}
