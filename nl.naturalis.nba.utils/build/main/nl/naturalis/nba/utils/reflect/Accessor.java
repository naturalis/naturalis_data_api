package nl.naturalis.nba.utils.reflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Accessor {

	private static HashMap<Class<?>, Accessor> accessors = new HashMap<>();

	public static Accessor forClass(Class<?> cls)
	{
		Accessor a = accessors.get(cls);
		if (a == null) {
			a = new Accessor(cls);
			accessors.put(cls, a);
		}
		return a;
	}

	private HashMap<String, Field> fields = new HashMap<>();

	private Accessor(Class<?> cls)
	{
		for (Field f : getAllFields(cls)) {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			fields.put(f.getName(), f);
		}
	}

	public Object get(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.get(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public byte getByte(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getByte(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public char getChar(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getChar(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public double getDouble(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getDouble(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public float getFloat(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getFloat(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public int getInt(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getInt(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public long getLong(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getLong(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public short getShort(Object obj, String field)
	{
		Field f = fields.get(field);
		if (f == null)
			throw new RuntimeException("No such field: " + field);
		try {
			return f.getShort(obj);
		}
		catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static List<Field> getAllFields(Class<?> forClass)
	{
		List<Field> fields = new ArrayList<Field>();
		while (forClass != Object.class) {
			fields.addAll(Arrays.asList(forClass.getDeclaredFields()));
			forClass = forClass.getSuperclass();
		}
		return fields;
	}
}
