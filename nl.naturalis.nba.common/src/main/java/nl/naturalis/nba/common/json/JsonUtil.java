package nl.naturalis.nba.common.json;

import static com.fasterxml.jackson.core.util.DefaultIndenter.SYS_LF;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.Path;

public class JsonUtil {

	/**
	 * The value returned by the {@link JsonUtil#readField(String, String)
	 * readField} methods if the field does not exist in the provided JSON
	 * source.
	 */
	public static final Object MISSING_VALUE = new Object();

	private static final ObjectMapperLocator oml;
	private static final TypeReference<Map<String, Object>> typeRef;
	private static final PrettyPrinter printer;
	private static final Charset UTF8;

	static {
		oml = ObjectMapperLocator.getInstance();
		typeRef = new TypeReference<Map<String, Object>>() {};
		printer = getPrettyPrinter();
		UTF8 = Charset.forName("UTF-8");
	}

	/*
	 * DESERIALIZATION
	 */

	public static <T> T deserialize(byte[] json, Class<T> type)
	{
		ObjectMapper om = oml.getObjectMapper(type);
		return deserialize(om, json, type);
	}

	public static <T> T deserialize(ObjectMapper om, byte[] json, Class<T> type)
	{
		try {
			return om.readValue(json, type);
		}
		catch (IOException e) {
			throw new JsonDeserializationException(e);
		}
	}

	public static <T> T deserialize(String json, Class<T> type)
	{
		return deserialize(json.getBytes(UTF8), type);
	}

	public static Map<String, Object> deserialize(byte[] json)
	{
		ObjectMapper om = oml.getObjectMapper(Map.class);
		try {
			return om.readValue(json, typeRef);
		}
		catch (IOException e) {
			throw new JsonDeserializationException(e);
		}
	}

	public static Map<String, Object> deserialize(String json)
	{
		return deserialize(json.getBytes(UTF8));
	}

	public static Map<String, Object> deserialize(InputStream src)
	{
		ObjectMapper om = oml.getObjectMapper(Map.class);
		try {
			return om.readValue(src, typeRef);
		}
		catch (IOException e) {
			throw new JsonDeserializationException(e);
		}
	}

	public static <T> T deserialize(InputStream src, Class<T> type)
	{
		ObjectMapper om = oml.getObjectMapper(type);
		try {
			return om.readValue(src, type);
		}
		catch (IOException e) {
			throw new JsonDeserializationException(e);
		}
	}

	/**
	 * Returns the value of specified field from the specified JSON source. Use
	 * the dot notation to access nested fields (e.g.
	 * {@code sourceSystem.name}). Array elements can be accessed by inserting
	 * the array index after the field name (e.g.
	 * {@code identifications.0.defaultClassification.genus}).
	 * 
	 * @see #readField(Map, String[])
	 * 
	 * @param json
	 * @param path
	 * @return
	 */
	public static Object readField(byte[] json, String path)
	{
		return readField(deserialize(json), path);
	}

	/**
	 * Returns the value of specified field from the specified JSON source. Use
	 * the dot notation to access nested fields (e.g.
	 * {@code sourceSystem.name}). Array elements can be accessed by inserting
	 * the array index after the field name (e.g.
	 * {@code identifications.0.defaultClassification.genus}).
	 * 
	 * @see #readField(Map, String[])
	 * 
	 * @param json
	 * @param path
	 * @return
	 */
	public static Object readField(String json, String path)
	{
		return readField(deserialize(json), path);
	}

	/**
	 * Returns the value of specified field from the specified JSON source. Use
	 * the dot notation to access nested fields (e.g.
	 * {@code sourceSystem.name}). Array elements can be accessed by inserting
	 * the array index after the field name (e.g.
	 * {@code identifications.0.defaultClassification.genus}).
	 * 
	 * @see #readField(Map, String[])
	 * 
	 * @param src
	 * @param path
	 * @return
	 */
	public static Object readField(InputStream src, String path)
	{
		return readField(deserialize(src), path);
	}

	/**
	 * Returns the value of specified field from the specified map. Use the dot
	 * notation to access nested fields (e.g. {@code sourceSystem.name}). Array
	 * elements can be accessed by inserting the array index after the field
	 * name (e.g. {@code identifications.0.defaultClassification.genus}).
	 * 
	 * @see #readField(Map, String[])
	 * 
	 * @param map
	 * @param path
	 * @return
	 */
	public static Object readField(Map<String, Object> map, String path)
	{
		return readField(map, new Path(path));
	}

	/**
	 * Extracts a value from the specified map using the specified path to
	 * navigate the map. When Jackson converts JSON to a {@link Map}, it nests
	 * {@code Map<String, Object>} objects within {@code Map<String, Object>}
	 * objects. The {@code path} array is supposed to contain field names at
	 * successively deeper levels. No exception is thrown if a field name does
	 * not exist, because JSON may be sparsely populated ({@code null} fields
	 * being omitted). Instead, the special value {@link #MISSING_VALUE} is
	 * returned. You SHOULD check for this value, and you should do so comparing
	 * references rather than using the {@link equals()} method.
	 * <h3>List access</h3><br>
	 * JSON arrays are converted to {@link ArrayList} instances by Jackson. You
	 * can access list elements by inserting the index of the desired element
	 * into the path. For example:<br>
	 * <code>
	 * String[] path = new String[] {"identifications", "0", "defaultClassification", "genus"};
	 * </code><br>
	 * If the specified index is out of bounds, the special value
	 * {@link #MISSING_VALUE} is returned. You cannot directly access elements
	 * in multi-dimensional arrays using this method.
	 * 
	 * @param map
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object readField(Map<String, Object> map, Path path)
	{
		for (int i = 0; i < path.countElements(); ++i) {
			Object val = map.get(path.getElement(i));
			if (val == null)
				return map.containsKey(path.getElement(i)) ? null : MISSING_VALUE;
			if (i == path.countElements() - 1)
				return val;
			if (val instanceof List) {
				try {
					int idx = Integer.parseInt(path.getElement(i + 1));
					List<?> list = (List<?>) val;
					if (idx >= list.size())
						return MISSING_VALUE;
					val = list.get(idx);
					if (++i == path.countElements() - 1)
						return val;
				}
				catch (NumberFormatException e) {
					String fmt = "Missing array index after %s in path %s";
					String msg = String.format(fmt, path.getElement(i), path);
					throw new JsonDeserializationException(msg);
				}
			}
			map = (Map<String, Object>) val;
		}
		/* Won't get here */ return null;
	}

	public static <T> T convert(Map<String, Object> map, Class<T> type)
	{
		ObjectMapper om = oml.getObjectMapper(type);
		return om.convertValue(map, type);
	}

	/*
	 * SERIALIZATION
	 */

	public static byte[] serialize(Object obj)
	{
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		om.setSerializationInclusion(Include.NON_NULL);
		try {
			return om.writeValueAsBytes(obj);
		}
		catch (JsonProcessingException e) {
			throw new JsonSerializationException(e);
		}
	}

	public static String toJson(Object obj)
	{
		try {
			return new String(serialize(obj), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toPrettyJson(Object obj)
	{
		return toPrettyJson(obj, true);
	}

	public static String toPrettyJson(Object obj, boolean terse)
	{
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		if (terse) {
			om.setSerializationInclusion(Include.NON_NULL);
		}
		else {
			om.setSerializationInclusion(Include.ALWAYS);
		}
		try {
			return om.writer(printer).writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new JsonSerializationException(e);
		}
	}

	/**
	 * Concatenates the specified path elements to a path string without array
	 * indices. For example, if you pass {"identifications", "0",
	 * "defaultClassification", "kingdom"}, then
	 * "identifications.defaultClassification.kingdom" is returned.
	 * 
	 * @param pathElements
	 * @return
	 */
	public static String getPurePath(String[] pathElements)
	{
		StringBuilder sb = new StringBuilder(50);
		for (String element : pathElements) {
			try {
				Integer.parseInt(element);
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the specified path without any array indices.
	 */
	public static String getPurePath(String path)
	{
		return getPurePath(path.split("\\."));
	}

	private JsonUtil()
	{
	}

	private static DefaultPrettyPrinter getPrettyPrinter()
	{
		DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", SYS_LF);
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(indenter);
		printer.indentArraysWith(indenter);
		return printer;
	}

}
