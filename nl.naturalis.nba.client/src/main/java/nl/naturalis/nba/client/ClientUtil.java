package nl.naturalis.nba.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;

class ClientUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ClientUtil.class);
	private static final ObjectMapperLocator oml = ObjectMapperLocator.getInstance();

	public static String getString(byte[] response)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(String.class);
			return om.readValue(response, String.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static int getInt(byte[] response)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(int.class);
			return om.readValue(response, int.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static boolean getBoolean(byte[] response)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(boolean.class);
			return om.readValue(response, boolean.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static <T> T getObject(byte[] response, Class<T> type)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(type);
			return om.readValue(response, type);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static void printTerse(Object obj)
	{
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		om.setSerializationInclusion(Include.NON_NULL);
		try {
			String s = om.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			System.out.println(s);
		}
		catch (JsonProcessingException e) {
			throw new ClientException(e);
		}
	}

	public static void printFull(Object obj)
	{
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		om.setSerializationInclusion(Include.ALWAYS);
		try {
			String s = om.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
			System.out.println(s);
		}
		catch (JsonProcessingException e) {
			throw new ClientException(e);
		}
	}

}
