package nl.naturalis.nba.client;

import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_NO_CONTENT;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.common.rest.ServerInfo;
import nl.naturalis.nba.utils.ClassUtil;
import nl.naturalis.nba.utils.StringUtil;
import nl.naturalis.nba.utils.debug.BeanPrinter;

/**
 * A {@link RuntimeException} thrown when an error condition arose in
 * server-side code.
 * 
 * @author Ayco Holleman
 *
 */
public class ServerException extends RuntimeException {

  private static final long serialVersionUID = 5323619448077539890L;

  @SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(ServerException.class);

	static ServerException newServerException(int status, byte[] response)
	{
		if (status == HTTP_NO_CONTENT) {
			String msg;
			if (response == null || response.length == 0) {
				msg = "The NBA responded with HTTP 204 (No Content), probably "
						+ "because the Java method to which your request was "
						+ "dispatched returned null. Neither of these things "
						+ "is supposed to happen. You found a bug!";
			}
			else {
				msg = "The NBA responded with HTTP 204 (No Content), but actually "
						+ "did return content. You found a bug!";
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
				 * Ah, well, this one isn't coming from NBA server-side code.
				 * It's Wildfly informing the client it has specified a
				 * non-existent end point. This is a bug in the Java client.
				 */
				throw new NoSuchServiceException();
			}
		}
		ServerInfo serverInfo = ClientUtil.getObject(response, ServerInfo.class);
		return new ServerException(serverInfo);
	}

	private ServerInfo serverInfo;

	private ServerException(ServerInfo serverInfo)
	{
		super(serverInfo.getException().getMessage());
		this.serverInfo = serverInfo;
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

	ServerInfo getServerInfo()
	{
		return serverInfo;
	}

	boolean was(Class<? extends Throwable> exception)
	{
		return ClassUtil.isA(serverInfo.getException().getType(), exception);
	}

}
