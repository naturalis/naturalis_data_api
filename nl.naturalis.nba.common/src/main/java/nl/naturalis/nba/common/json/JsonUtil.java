package nl.naturalis.nba.common.json;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final ObjectMapperLocator oml = ObjectMapperLocator.getInstance();

	public static byte[] serialize(Object obj)
	{
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		try {
			return om.writeValueAsBytes(obj);
		}
		catch (JsonProcessingException e) {
			throw new JsonSerializationException(e);
		}
	}

	public static <T> T deserialize(byte[] bytes, Class<T> type)
	{
		ObjectMapper om = oml.getObjectMapper(type);
		try {
			return om.readValue(bytes, type);
		}
		catch (IOException e) {
			throw new JsonDeserializationException(e);
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

	public static <T> T fromJson(String json, Class<T> type)
	{
		try {
			return deserialize(json.getBytes("UTF-8"), type);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T convert(Map<String, Object> map, Class<T> type)
	{
		ObjectMapper om = oml.getObjectMapper(type);
		return om.convertValue(map, type);
	}

	private JsonUtil()
	{
	}

}
