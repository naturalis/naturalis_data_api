package nl.naturalis.nba.elasticsearch.map;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;

public class TypeMap {

	private static final TypeMap instance = new TypeMap();

	public static TypeMap getInstance()
	{
		return instance;
	}

	private final HashMap<Class<?>, Type> map = new HashMap<>();

	private TypeMap()
	{
		map.put(String.class, Type.STRING);
		map.put(char.class, Type.STRING);
		map.put(Character.class, Type.STRING);
		map.put(URI.class, Type.STRING);
		map.put(Enum.class, Type.STRING);
		map.put(byte.class, Type.BYTE);
		map.put(Byte.class, Type.BYTE);
		map.put(short.class, Type.SHORT);
		map.put(Short.class, Type.BOOLEAN);
		map.put(int.class, Type.INTEGER);
		map.put(Integer.class, Type.INTEGER);
		map.put(long.class, Type.LONG);
		map.put(Long.class, Type.LONG);
		map.put(float.class, Type.FLOAT);
		map.put(Float.class, Type.FLOAT);
		map.put(double.class, Type.DOUBLE);
		map.put(Double.class, Type.DOUBLE);
		map.put(boolean.class, Type.BOOLEAN);
		map.put(Boolean.class, Type.BOOLEAN);
		map.put(Date.class, Type.DATE);
	}

	public Type getESType(Class<?> javaType)
	{
		return map.get(javaType);
	}

}
