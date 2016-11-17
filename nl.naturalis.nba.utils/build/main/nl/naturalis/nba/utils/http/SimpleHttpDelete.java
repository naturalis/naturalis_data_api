package nl.naturalis.nba.utils.http;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

public class SimpleHttpDelete extends SimpleHttpRequest {
	
	

	public SimpleHttpDelete()
	{
		super();
	}


	public SimpleHttpDelete(boolean shareHttpClient)
	{
		super(shareHttpClient);
	}


	@Override
	protected HttpRequestBase createRequest()
	{
		return new HttpDelete();
	}

}
