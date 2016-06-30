package nl.naturalis.nba.common.json;

import static com.fasterxml.jackson.core.util.DefaultIndenter.SYS_LF;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
	private static final PrettyPrinter printer = getPrettyPrinter();

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

	private static DefaultPrettyPrinter getPrettyPrinter()
	{
		DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", SYS_LF);
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(indenter);
		printer.indentArraysWith(indenter);
		return printer;
	}

}
