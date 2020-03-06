package nl.naturalis.nba.rest.util;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.text.StringEscapeUtils;

import nl.naturalis.nba.rest.exception.RESTException;

public class ResourceUtil {

	/**
	 * The JSON media type including the charset specification:
	 * "application/json;charset=UTF-8". Some (older) browsers and REST clients
	 * seem to require the explicit charset specification in order to process
	 * the JSON response properly. Therefore resource methods should not
	 * {@link Produces produce} {@link MediaType#APPLICATION_JSON}, since that
	 * constant does not include the charset specification.
	 */
	public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";
	
	/**
	 * The NDJSON format (also called Newline delimited JSON) is a convenient 
	 * format for storing or streaming structured data that may be processed one record 
	 * at a time. Its specs ({@link Produces https://github.com/ndjson/ndjson-spec#33-mediatype-and-file-extensions}) 
	 * define the media-type as: "application/x-ndjson". We have chosen to follow the specs,
	 * although there appears to be some discussion over whether the type shouldn't be 
	 * defined as: "application/ndjson".
	 */
	public static final String NDJSON_CONTENT_TYPE = "application/x-ndjson;charset=UTF-8";

	public static final String TEXT_CONTENT_TYPE = "text/plain;charset=UTF-8";
	
	public static final String ZIP_CONTENT_TYPE = "application/zip";
	
	public static RESTException handleError(UriInfo request, Throwable throwable)
	{
		if (throwable instanceof RESTException) {
			return (RESTException) throwable;
		}
		return new RESTException(request, throwable);
	}

	/**
	 * Quotes and escapes the specified {@link String} so it becomes a JSON
	 * string value. Strangely, when resource methods that {@link Produces
	 * produce} application/json return a string, Wildfly/RESTeasy does not
	 * automatically JSONify the string. Hence this method.
	 * 
	 * @param s - ...
	 * @return String
	 */
	@SuppressWarnings("unused")
	public static String stringAsJson(String s)
	{
		return "\"" + StringEscapeUtils.escapeJson(s) + "\"";
	}

}
