package nl.naturalis.nba.dao.es.map;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.naturalis.nba.api.model.GeoShape;

/**
 * Maps Java types to Elasticsearch types and vice versa.
 * 
 * @author Ayco Holleman
 *
 */
public class DataTypeMap {

	private static final DataTypeMap instance = new DataTypeMap();

	public static DataTypeMap getInstance()
	{
		return instance;
	}

	private final HashMap<Class<?>, ESDataType> java2es = new HashMap<>();
	private final EnumMap<ESDataType, Set<Class<?>>> es2java = new EnumMap<>(ESDataType.class);

	private DataTypeMap()
	{
		/* Stringy types */
		java2es.put(String.class, ESDataType.STRING);
		java2es.put(char.class, ESDataType.STRING);
		java2es.put(Character.class, ESDataType.STRING);
		java2es.put(URI.class, ESDataType.STRING);
		java2es.put(URL.class, ESDataType.STRING);
		java2es.put(Enum.class, ESDataType.STRING);
		/* Number types */
		java2es.put(byte.class, ESDataType.BYTE);
		java2es.put(Byte.class, ESDataType.BYTE);
		java2es.put(short.class, ESDataType.SHORT);
		java2es.put(Short.class, ESDataType.BOOLEAN);
		java2es.put(int.class, ESDataType.INTEGER);
		java2es.put(Integer.class, ESDataType.INTEGER);
		java2es.put(long.class, ESDataType.LONG);
		java2es.put(Long.class, ESDataType.LONG);
		java2es.put(float.class, ESDataType.FLOAT);
		java2es.put(Float.class, ESDataType.FLOAT);
		java2es.put(double.class, ESDataType.DOUBLE);
		java2es.put(Double.class, ESDataType.DOUBLE);
		/* Boolean types */
		java2es.put(boolean.class, ESDataType.BOOLEAN);
		java2es.put(Boolean.class, ESDataType.BOOLEAN);
		/* Date types */
		java2es.put(Date.class, ESDataType.DATE);
		/* Other types */
		java2es.put(GeoShape.class, ESDataType.GEO_SHAPE);

		/* Create reverse map */
		for (Map.Entry<Class<?>, ESDataType> entry : java2es.entrySet()) {
			Class<?> javaType = entry.getKey();
			ESDataType esType = entry.getValue();
			Set<Class<?>> javaTypes = es2java.getOrDefault(esType, new HashSet<>());
			javaTypes.add(javaType);
		}
	}

	/**
	 * Returns the Elasticsearch data type corresponding to the specified Java
	 * type. If none is found, the superclass of the specified type is checked
	 * to see if it corresponds to an Elasticsearch data type, and so on until
	 * (but not including) the {@link Object} class.
	 * 
	 * @param javaType
	 * @return
	 */
	public ESDataType getESType(Class<?> javaType)
	{
		while (javaType != Object.class) {
			ESDataType t = java2es.get(javaType);
			if (t != null)
				return t;
			javaType = javaType.getSuperclass();
		}
		return null;
	}

	/**
	 * Returns all Java types that map to the specified Elasticsearch data type.
	 * 
	 * @param esType
	 * @return
	 */
	public Set<Class<?>> getJavaTypes(ESDataType esType)
	{
		return es2java.get(esType);
	}

}
