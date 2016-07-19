package nl.naturalis.nba.common.json;

import static com.fasterxml.jackson.core.util.DefaultIndenter.SYS_LF;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

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

	public static Object readField(byte[] json, String path)
	{
		return readField(deserialize(json), path);
	}

	public static Object readField(String json, String path)
	{
		return readField(deserialize(json), path);
	}

	public static Object readField(InputStream src, String path)
	{
		return readField(deserialize(src), path);
	}

	@SuppressWarnings("unchecked")
	public static Object readField(Map<String, Object> map, String path)
	{
		String[] chunks = path.split("\\.");
		for (int i = 0; i < chunks.length; i++) {
			String key = chunks[i];
			if (!map.containsKey(key)) {
				String msg = "No such field: \"" + key + "\"";
				throw new JsonDeserializationException(msg);
			}
			Object val = map.get(key);
			if (i == chunks.length - 1) {
				return val;
			}
			map = (Map<String, Object>) val;
		}
		assert (false);
		return null;
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
