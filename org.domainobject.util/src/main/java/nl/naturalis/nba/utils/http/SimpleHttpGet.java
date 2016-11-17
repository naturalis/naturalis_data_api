package nl.naturalis.nba.utils.http;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class SimpleHttpGet extends SimpleHttpRequest {

	public SimpleHttpGet()
	{
		super();
	}


	public SimpleHttpGet(boolean shareHttpClient)
	{
		super(shareHttpClient);
	}


	@Override
	protected HttpRequestBase createRequest()
	{
		return new HttpGet();
	}

}
