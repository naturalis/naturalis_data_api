package nl.naturalis.nba.elasticsearch.map;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;

public class DataTypeMap {

	private static final DataTypeMap instance = new DataTypeMap();

	public static DataTypeMap getInstance()
	{
		return instance;
	}

	private final HashMap<Class<?>, ESDataType> map = new HashMap<>();

	private DataTypeMap()
	{
		map.put(String.class, ESDataType.STRING);
		map.put(char.class, ESDataType.STRING);
		map.put(Character.class, ESDataType.STRING);
		map.put(URI.class, ESDataType.STRING);
		map.put(Enum.class, ESDataType.STRING);
		map.put(byte.class, ESDataType.BYTE);
		map.put(Byte.class, ESDataType.BYTE);
		map.put(short.class, ESDataType.SHORT);
		map.put(Short.class, ESDataType.BOOLEAN);
		map.put(int.class, ESDataType.INTEGER);
		map.put(Integer.class, ESDataType.INTEGER);
		map.put(long.class, ESDataType.LONG);
		map.put(Long.class, ESDataType.LONG);
		map.put(float.class, ESDataType.FLOAT);
		map.put(Float.class, ESDataType.FLOAT);
		map.put(double.class, ESDataType.DOUBLE);
		map.put(Double.class, ESDataType.DOUBLE);
		map.put(boolean.class, ESDataType.BOOLEAN);
		map.put(Boolean.class, ESDataType.BOOLEAN);
		map.put(Date.class, ESDataType.DATE);
	}

	public ESDataType getESType(Class<?> javaType)
	{
		return map.get(javaType);
	}

}
