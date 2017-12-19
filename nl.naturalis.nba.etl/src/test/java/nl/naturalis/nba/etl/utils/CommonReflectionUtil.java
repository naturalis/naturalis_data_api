package nl.naturalis.nba.etl.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class CommonReflectionUtil {


    public static <T> T genericReflectionMethod(Object param, Class<T> paramClass, Object objectType, Object methodname) throws Exception {
        T obj = null;
        Method method = null;
        if (param != null) {
            method = Class.forName(objectType.getClass().getName()).getDeclaredMethod(methodname.toString(), paramClass);
            method.setAccessible(true);
            obj = (T) method.invoke(objectType, param);
        } else {
            method = Class.forName(objectType.getClass().getName()).getDeclaredMethod(methodname.toString());
            method.setAccessible(true);
            obj = (T) method.invoke(objectType);
        }
        return obj;
    }

    /**
     * Generic Reflection method to access private filed.
     * 
     * @param <T>
     * 
     * @param CSVRecordInfo
     * @param brahmsSpecimenTransformer
     * @param methodName
     * 
     * @return Object
     * @throws Exception
     */

    public static <T> void setPrivateField(Class<T> className, Object obj, String fieldName, Object filedValue) throws Exception {

        Field field = className.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, filedValue);
    }

}
