package nl.naturalis.nba.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.common.json.ObjectMapperLocator;
import nl.naturalis.nba.utils.http.SimpleHttpException;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * 
 * @author Ayco Holleman
 *
 */
public class ClientUtil {

	private static final Logger logger = LogManager.getLogger(ClientUtil.class);
	private static final ObjectMapperLocator oml = ObjectMapperLocator.getInstance();

	/**
	 * Extracts and returns a string value from a JSON server response.
	 * 
	 * @param response
	 * @return
	 */
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

	/**
	 * Extracts and returns an integer from a JSON server response.
	 * 
	 * @param response
	 * @return
	 */
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

	/**
	 * Extracts and returns a boolean value from a JSON server response.
	 * 
	 * @param response
	 * @return
	 */
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

	/**
	 * Converts the specified JSON server response to an object of the specified
	 * type.
	 * 
	 * @param response
	 * @param type
	 * @return
	 */
	public static <T> T getObject(byte[] response, Class<T> type)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(type);
			return om.readValue(response, type);
		}
		catch (JsonMappingException e0) {
			String fmt = "Could not convert JSON response to %s:\n\n%s\n";
			String msg = String.format(fmt, type.getName(), response);
			throw new ClientException(msg);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	/**
	 * Converts the specified JSON server response to an object of the specified
	 * type.
	 * 
	 * @param response
	 * @param type
	 * @return
	 */
	public static <T> T getObject(byte[] response, TypeReference<T> type)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(type);
			return om.readValue(response, type);
		}
		catch (JsonMappingException e0) {
			String fmt = "Could not convert JSON response to instance of %s. Response was:\n%s";
			String msg = String.format(fmt, type.getType().getTypeName(), response);
			throw new ClientException(msg);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	/**
	 * Converts the specified JSON server response to an object of the specified
	 * type.
	 * 
	 * @param response
	 * @param type
	 * @return
	 */
	public static <T> QueryResult<T> getQueryResult(byte[] response,
			TypeReference<QueryResult<T>> type)
	{
		try {
			ObjectMapper om = oml.getObjectMapper(type);
			return om.readValue(response, type);
		}
		catch (JsonMappingException e0) {
			String fmt = "Could not convert JSON response to instance of %s. Response was:\n%s";
			String msg = String.format(fmt, type.getType().getTypeName(), response);
			throw new ClientException(msg);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

	/**
	 * Converts the specified object to JSON and writes it to
	 * {@code System.out}. Fields whose value is {@code null} values are
	 * ignored.
	 * 
	 * @param obj
	 */
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

	/**
	 * Converts the specified object to JSON and writes it to
	 * {@code System.out}. Fields whose value is {@code null} values are
	 * included in the output.
	 * 
	 * @param obj
	 */
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

	static SimpleHttpRequest sendRequest(SimpleHttpRequest request)
	{
		URI uri = getURI(request);
		logger.info("Sending {} request:\n{}", request.getMethod(), uri);
		try {
			request.execute();
		}
		catch (Throwable t) {
			if (t instanceof SimpleHttpException) {
				if (t.getMessage().indexOf("Connection refused") != -1) {
					String fmt = "NBA server down or invalid base URL: %s";
					String msg = String.format(fmt, request.getBaseUrl());
					throw new ClientException(msg);
				}
			}
			throw t;
		}
		return request;
	}

	private static URI getURI(SimpleHttpRequest request)
	{
		try {
			return request.createUri();
		}
		catch (URISyntaxException e) {
			String fmt = "Invalid URL (path: \"%s\"; query: \"%s\")";
			String msg = String.format(fmt, request.getPath(), request.getQuery());
			throw new ClientException(msg);
		}
	}

}
