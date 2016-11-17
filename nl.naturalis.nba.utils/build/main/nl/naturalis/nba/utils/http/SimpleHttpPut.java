package nl.naturalis.nba.utils.http;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * A wrapper around Apache's HttpPut catering for its most common use cases.
 * 
 * @author Ayco Holleman
 * 
 */
public class SimpleHttpPut extends SimpleHttpRequest {

	public SimpleHttpPut()
	{
		super();
	}

	public SimpleHttpPut(boolean shareHttpClient)
	{
		super(shareHttpClient);
	}

	protected HttpRequestBase createRequest()
	{
		return new HttpPut();
	}

}
