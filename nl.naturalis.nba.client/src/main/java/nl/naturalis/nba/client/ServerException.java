package nl.naturalis.nba.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NO_CONTENT;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.StringUtil;
import org.domainobject.util.debug.BeanPrinter;

/**
 * An {@code NBAResourceException} is the client-side mirror of a
 * {@code RESTException} in the service layer. All resource methods must throw a
 * {@code RESTException} in case of an error condition and trap/wrap any other
 * {@code Exception}. Both {@code RESTException} and
 * {@code NBAResourceException} contain a field storing information about what
 * went wrong while processing the request ({@code info} and {@code serverInfo}
 * respectively). These fields are equivalent.
 * 
 * @author Ayco Holleman
 *
 */
public class ServerException extends RuntimeException {

	private static final Logger logger = LogManager.getLogger(ServerException.class);

	@SuppressWarnings("unchecked")
	static ServerException newServerException(int status, byte[] response)
	{
		if (status == HTTP_NO_CONTENT) {
			String msg;
			if (response == null || response.length == 0) {
				msg = "The NBA responded with HTTP 204 (No Content), probably "
						+ "because the Java method to which your request was "
						+ "dispatched returned null. But neither is supposed "
						+ "to happen. A.k.a. you found a bug";
			}
			else {
				msg = "The NBA responded with HTTP 204 (No Content), but actually "
						+ "did return content. A.k.a. you found a bug";
			}
			throw new ClientException(msg);
		}
		if (response == null) {
			String fmt = "NBA responded with status code %s, but response body was empty";
			throw new ClientException(String.format(fmt, status));
		}
		String s = StringUtil.toString(response);
		if (s.startsWith("javax.ws.rs.NotFoundException")) {
			if (s.indexOf("Could not find resource for full path") != -1) {
				/*
				 * Ah, well, this one isn't coming from the bowels of NBA server
				 * side code. It's Wildfly informing the client it has specified
				 * a non-existent end point. This should basically count as a
				 * bug in the Java client.
				 */
				throw new NoSuchServiceException();
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Deserializing NBA exception: {}", s);
		}
		LinkedHashMap<String, Object> serverInfo;
		serverInfo = ClientUtil.getObject(response, LinkedHashMap.class);
		LinkedHashMap<String, Object> exception;
		exception = (LinkedHashMap<String, Object>) serverInfo.get("exception");
		String message = (String) exception.get("message");
		return new ServerException(message, serverInfo);
	}

	private final Map<String, Object> serverInfo;

	private ServerException(String message, Map<String, Object> serverInfo)
	{
		super(message);
		this.serverInfo = serverInfo;
	}

	public Map<String, Object> getServerInfo()
	{
		return serverInfo;
	}

	public String getServerInfoAsString()
	{
		StringWriter sw = new StringWriter(2048);
		BeanPrinter bp = new BeanPrinter(new PrintWriter(sw));
		bp.dump(serverInfo);
		return sw.toString();
	}

	@Override
	public String toString()
	{
		return getServerInfoAsString();
	}

}
