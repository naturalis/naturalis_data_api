package nl.naturalis.nba.utils.http;

import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;

public class SimpleHttpHead extends SimpleHttpRequest {
	
	

	public SimpleHttpHead()
	{
		super();
	}


	public SimpleHttpHead(boolean shareHttpClient)
	{
		super(shareHttpClient);
	}


	@Override
	protected HttpRequestBase createRequest()
	{
		return new HttpHead();
	}

}
