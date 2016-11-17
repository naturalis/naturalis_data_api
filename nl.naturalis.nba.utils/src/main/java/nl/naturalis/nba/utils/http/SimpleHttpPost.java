package nl.naturalis.nba.utils.http;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;

public class SimpleHttpPost extends SimpleHttpRequest {

	public SimpleHttpPost()
	{
		super();
	}

	public SimpleHttpPost(boolean shareHttpClient)
	{
		super(shareHttpClient);
	}

	/**
	 * Adds a form parameter.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public SimpleHttpRequest addFormParam(String key, String value)
	{
		if (formParams == null) {
			formParams = new ArrayList<>(8);
		}
		formParams.add(new String[] { key, value });
		return this;
	}

	@Override
	protected HttpRequestBase createRequest()
	{
		return new HttpPost();
	}

}
