package nl.naturalis.nba.client;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class ClientUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ClientUtil.class);
	private static final ObjectMapper objectMapper = NBAObjectMapperFactory.getObjectMapper();

	public static String getString(byte[] response)
	{
		try {
			return objectMapper.readValue(response, String.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static int getInt(byte[] response)
	{
		try {
			return objectMapper.readValue(response, int.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static boolean getBoolean(byte[] response)
	{
		try {
			return objectMapper.readValue(response, boolean.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static <T> T getObject(byte[] response, Class<T> type)
	{
		try {
			if (response == null || response.length == 0) {
				return null;
			}
			return objectMapper.readValue(response, type);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	public static void printTerse(Object obj)
	{
		ObjectMapper om = NBAObjectMapperFactory.getObjectMapper();
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
		ObjectMapper om = NBAObjectMapperFactory.getObjectMapper();
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
