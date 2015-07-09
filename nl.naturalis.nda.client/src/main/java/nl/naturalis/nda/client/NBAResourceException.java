package nl.naturalis.nda.client;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class NBAResourceException extends Exception {

	private static final long serialVersionUID = -8246486578070786218L;


	@SuppressWarnings("unchecked")
	static NBAResourceException createFromResponse(byte[] response)
	{
		LinkedHashMap<String, Object> serverInfo = ClientUtil.getObject(response, LinkedHashMap.class);
		LinkedHashMap<String, Object> exception = (LinkedHashMap<String, Object>) serverInfo.get("exception");
		String message = (String) exception.get("message");
		return new NBAResourceException(message, serverInfo);
	}

	private final Map<String, Object> serverInfo;


	public NBAResourceException(String message, Map<String, Object> serverInfo)
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
