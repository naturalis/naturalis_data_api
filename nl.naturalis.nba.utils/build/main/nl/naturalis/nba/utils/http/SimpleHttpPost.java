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
	 * Adds a form parameter. The first time you call this method the
	 * Content-Type header will tacitly be set to
	 * {@link SimpleHttpRequest#CT_X_WWW_FORM_URLENCODED
	 * application/x-www-form-urlencoded}.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public SimpleHttpRequest addFormParam(String key, String value)
	{
		if (requestBody != null) {
			throw new SimpleHttpException("Request body and form parameters cannot both be set");
		}
		if (formParams == null) {
			formParams = new ArrayList<>(8);
			setContentType(CT_X_WWW_FORM_URLENCODED);
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
