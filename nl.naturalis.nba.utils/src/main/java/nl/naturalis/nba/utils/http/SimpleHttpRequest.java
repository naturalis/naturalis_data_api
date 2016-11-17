package nl.naturalis.nba.utils.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import nl.naturalis.nba.utils.IOUtil;

/**
 * Abstract base class for all HTTP request wrappers in this package (GET, POST,
 * etc). You can repeatedly alter different properties of the request (base URL,
 * path segment, query string, fragment, headers, body) and then execute the
 * request again. Except for the request body, these properties are <i>not</i>
 * reset to some default value (e.g. {@code null}) after the request is
 * executed.
 * 
 * @author Ayco Holleman
 * 
 */
public abstract class SimpleHttpRequest {

	public static final int HTTP_OK = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_NOT_FOUND = 404;

	/**
	 * Content-Type header key: &#34;Content-Type&#34;.
	 */
	public static final String HDR_CONTENT_TYPE = "Content-Type";
	/**
	 * Accept header key: &#34;Accept&#34;
	 */
	public static final String HDR_ACCEPT = "Accept";
	/**
	 * Content-Type header value: &#34;text/plain&#34;.
	 */
	public static final String CT_TEXT_PLAIN = "text/plain";
	/**
	 * Content-Type header value: &#34;application/json&#34;.
	 */
	public static final String CT_APPLICATION_JSON = "application/json";
	/**
	 * Content-Type header value: &#34;application/x-www-form-urlencoded&#34;.
	 */
	public static final String CT_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

	private static final String CHARSET_UTF8 = "UTF-8";

	private static HttpClient sharedHttpClient;

	protected String baseUrl;
	protected String path;
	protected String query;
	protected String fragment;
	protected String requestBody;
	protected String charset;
	protected LinkedHashMap<String, String> headers;
	protected ArrayList<String[]> queryParams;
	protected ArrayList<String[]> formParams;

	private final boolean shareHttpClient;

	private HttpClient httpClient;
	private HttpRequestBase request;
	private HttpResponse httpResponse;

	/**
	 * Create a {@code SimpleHttpRequest} that will share Apache's
	 * {@code HttpClient} with other instances.
	 * 
	 * @see SimpleHttpRequest#SimpleHttpRequest(boolean)
	 */
	public SimpleHttpRequest()
	{
		this(true);
	}

	/**
	 * Create a {@code SimpleHttpRequest}. When specifying true for
	 * {@code shareHttpClient}, this instance will share Apache's
	 * {@code HttpClient} with other instances of {@code SimpleHttpRequest}.
	 * When specifying false, this instance will use a privately maintained
	 * {@code HttpClient}. The content type of the request body (if any) is
	 * initialized to text/plain. The character set of the request URL and
	 * request body is initialized to UTF-8.
	 * 
	 * 
	 * @param shareHttpClient
	 *            Whether or not to share the @code HttpClient} with other
	 *            instances
	 */
	public SimpleHttpRequest(boolean shareHttpClient)
	{
		this.shareHttpClient = shareHttpClient;
		setContentType(CT_TEXT_PLAIN);
		charset = CHARSET_UTF8;
	}

	/**
	 * Reset all request and response data (baseUrl, path, query string,
	 * fragment, request headers, body and character set). The content type of
	 * the request body (if any) is initialized to text/plain. The character set
	 * of the request URL and request body is initialized to UTF-8.
	 */
	public void reset()
	{
		baseUrl = null;
		path = null;
		query = null;
		queryParams = null;
		formParams = null;
		fragment = null;
		requestBody = null;
		httpResponse = null;
		headers = null;
		charset = CHARSET_UTF8;
		setContentType(CT_TEXT_PLAIN);
		if (request != null) {
			request.reset();
		}
	}

	/**
	 * Sets the base URL (scheme, user info, host, port). You can, in fact, also
	 * use this method to set the entire URL, including path, query and
	 * fragment. Or you can use it to set the base URL plus the context root. In
	 * short, this method simply allows you to set the start of the URL. This is
	 * the only property that <b>must</b> be set in order to successfully
	 * {@link #execute() execute} an HTTP request.
	 * 
	 * @param baseUrl
	 *            The base URL or complete URL
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
		return this;
	}

	/**
	 * Returns the base URL for the request. See {@link #setBaseUrl(String)}.
	 * 
	 * @return
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Sets the path following the base URL. If the base URL already contained a
	 * path, the path provided here will be appended to it. If you pass
	 * {@code null} to this method, the base URL's path segment (if any) is
	 * preserved.
	 * 
	 * @param path
	 *            The path
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setPath(String path)
	{
		this.path = path;
		return this;
	}

	/**
	 * Adds a query parameter to the request URL. You must <i>either</i> use
	 * this method to build up your query string <i>or</i>
	 * {@link #setQuery(String) setQuery}.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public SimpleHttpRequest addQueryParam(String key, String value)
	{
		if (query != null) {
			throw new SimpleHttpException("Query string and query parameters cannot both be set");
		}
		if (queryParams == null) {
			queryParams = new ArrayList<>(8);
		}
		queryParams.add(new String[] { key, value });
		return this;
	}

	/**
	 * Returns the path following the base URL.
	 * 
	 * @return
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the query string of the URL. If the base URL already contained a
	 * query string, the query string provided here will be appended to it. If
	 * you pass {@code null} to this method, the base URL's query parameters (if
	 * any) are preserved.
	 * 
	 * @param query
	 *            The query string
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setQuery(String query)
	{
		if (queryParams != null) {
			throw new SimpleHttpException("Query string and query parameters cannot both be set");
		}
		this.query = query;
		return this;
	}

	/**
	 * Returns the query string.
	 * 
	 * @return
	 */
	public String getQuery()
	{
		return this.query;
	}

	/**
	 * Sets the URL fragment (everything following the # sign). If the base URL
	 * already contained a fragment part, this method will overwrite it!
	 * 
	 * @param fragment
	 *            The fragment part of the URL
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setFragment(String fragment)
	{
		this.fragment = fragment;
		return this;
	}

	/**
	 * Returns the fragment part of the URL.
	 * 
	 * @return
	 */
	public String getFragment()
	{
		return this.fragment;
	}

	/**
	 * Sets the {@code Content-Type} header on the request. Specifying
	 * {@code null} amounts to deleting the {@code Content-Type} header.
	 * 
	 * @param accept
	 *            The Content-Type Header
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */

	public SimpleHttpRequest setContentType(String contentType)
	{
		return setHeader(HDR_CONTENT_TYPE, contentType);
	}

	/**
	 * Returns the value of the {@code Content-Type} header, or {@code null} is
	 * this header is not set.
	 * 
	 * @return
	 */
	public String getContentType()
	{
		if (headers == null) {
			return null;
		}
		return headers.get(HDR_CONTENT_TYPE);
	}

	/**
	 * Sets the {@code Accept} header on the request.
	 * 
	 * @param accept
	 *            The Accept Header
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setAccept(String accept)
	{
		return setHeader(HDR_ACCEPT, accept);
	}

	/**
	 * Returns the value of the {@code Accept} header, or {@code null} is this
	 * header is not set.
	 * 
	 * @return
	 */
	public String getAccept()
	{
		if (headers == null) {
			return null;
		}
		return headers.get(HDR_ACCEPT);
	}

	/**
	 * Sets an arbitrary header on the request. Just like any other element the
	 * request (except the request body), the headers you set here will survive
	 * request executions. Passing {@code null} as value for the header will
	 * remove the header from the request.
	 * 
	 * @param key
	 *            The name of the header
	 * @param value
	 *            The value of the header
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setHeader(String key, String value)
	{
		if (value == null) {
			if (headers != null) {
				headers.remove(key);
			}
		}
		else {
			if (headers == null) {
				headers = new LinkedHashMap<String, String>();
			}
			headers.put(key, value);
		}
		return this;
	}

	/**
	 * Deletes the specified request header if it exists.
	 * 
	 * @param key
	 *            The name of the header
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest deleteHeader(String key)
	{
		if (headers != null) {
			headers.remove(key);
		}
		return this;
	}

	/**
	 * Sets the character set of the request URL and the request body.
	 * 
	 * @param charset
	 *            The character set
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setCharset(String charset)
	{
		this.charset = charset;
		return this;
	}

	/**
	 * Sets the request body. This is the only element of the HTTP request that
	 * is always reset to {@code null} once the request has been sent off to the
	 * server.
	 * 
	 * @param body
	 *            The contents of the request body
	 * @param contentType
	 *            The content type of the request body
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setRequestBody(String body, String contentType)
	{
		if (formParams != null) {
			throw new SimpleHttpException("Request body and form parameters cannot both be set");
		}
		this.requestBody = body;
		setContentType(contentType);
		return this;
	}

	/**
	 * Sets the request body.
	 * 
	 * @param body
	 *            The contents of the request body
	 * @param contentType
	 *            The content type of the request body
	 * @param charset
	 *            The character set used for the request body
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest setRequestBody(String body, String contentType, String charset)
	{
		this.requestBody = body;
		setContentType(contentType);
		this.charset = charset;
		return this;
	}

	/**
	 * Sends the HTTP request to the server.
	 * 
	 * @return This {@code SimpleHttpRequest}
	 */
	public SimpleHttpRequest execute()
	{
		httpResponse = null;
		request = createRequest();
		try {
			request.setURI(createUri());
		}
		catch (URISyntaxException e1) {
			throw new SimpleHttpException(e1);
		}
		try {
			if (headers != null) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					request.addHeader(header.getKey(), header.getValue());
				}
			}
			/*
			 * Is this a GET/PUT/POST request? Then we may have a request body.
			 */
			if (request instanceof HttpEntityEnclosingRequestBase) {
				if (formParams != null && formParams.size() != 0) {
					List<NameValuePair> nvps = new ArrayList<>();
					for (String[] param : formParams) {
						nvps.add(new BasicNameValuePair(param[0], param[1]));
					}
					HttpEntityEnclosingRequestBase r;
					r = (HttpEntityEnclosingRequestBase) request;
					r.setEntity(new UrlEncodedFormEntity(nvps));
				}
				else if (requestBody != null && requestBody.length() != 0) {
					HttpEntityEnclosingRequestBase r;
					r = (HttpEntityEnclosingRequestBase) request;
					StringEntity entity = new StringEntity(requestBody, charset);
					r.setEntity(entity);
				}
			}
			httpResponse = getHttpClient().execute(request);
			return this;
		}
		catch (IOException e) {
			throw new SimpleHttpException(e);
		}
		finally {
			requestBody = null;
		}
	}

	/**
	 * Get the response body, if any. Only applicable for POST and GET requests.
	 * This method can be called only once per request, and you must call either
	 * this method or {@link #getResponseBodyAsStream()}.
	 * 
	 * @return The response body or {@code null} if the response body was
	 *         generated.
	 */
	public byte[] getResponseBody()
	{
		try {
			checkResponse();
			HttpEntity e = httpResponse.getEntity();
			if (e != null && e.getContentLength() != 0) {
				return IOUtil.readAllBytes(e.getContent(), 4096);
			}
			return null;
		}
		catch (UnsupportedOperationException | IOException exc) {
			throw new SimpleHttpException(exc);
		}
		finally {
			request.reset();
		}
	}

	public InputStream getResponseBodyAsStream()
	{
		try {
			checkResponse();
			HttpEntity e = httpResponse.getEntity();
			if (e != null && e.getContentLength() != 0) {
				return e.getContent();
			}
			return null;
		}
		catch (UnsupportedOperationException | IOException exc) {
			throw new SimpleHttpException(exc);
		}
	}

	/**
	 * Get the raw HTTP response object from the underlying Apache HTTP
	 * Components implementation.
	 * 
	 * @return
	 */
	public HttpResponse getHttpResponse()
	{
		checkResponse();
		return httpResponse;
	}

	/**
	 * Get the error message, if any, returned by the server.
	 * 
	 * @return The error message
	 */
	public String getError()
	{
		checkResponse();
		return httpResponse.getStatusLine().getReasonPhrase();
	}

	/**
	 * Get the HTTP status for the request (e.g. 200 for successfully completed
	 * requests).
	 */
	public int getStatus()
	{
		checkResponse();
		return httpResponse.getStatusLine().getStatusCode();
	}

	/**
	 * Did the server respond with HTTP status 200?
	 * 
	 * @return
	 */
	public boolean isOK()
	{
		return getStatus() == HTTP_OK;
	}

	/**
	 * Returns the HTTP method (GET, PUT, DELETE, HEAD or NOT_SUPPORTED for HTTP
	 * methods not supported by the SimpleHttp library).
	 * 
	 * @return the HTTP method
	 */
	public HttpMethod getMethod()
	{
		if (getClass() == SimpleHttpGet.class) {
			return HttpMethod.GET;
		}
		if (getClass() == SimpleHttpPost.class) {
			return HttpMethod.POST;
		}
		if (getClass() == SimpleHttpPut.class) {
			return HttpMethod.PUT;
		}
		if (getClass() == SimpleHttpDelete.class) {
			return HttpMethod.DELETE;
		}
		if (getClass() == SimpleHttpHead.class) {
			return HttpMethod.HEAD;
		}
		return null;
	}

	/**
	 * Builds the URI that is to be sent off to the server. The URI is always
	 * built from scratch using the current values for the base URL, path, query
	 * string and fragment. This method is called by {@link #execute()}, but you
	 * can call it yourself to see which URI was constructed.
	 * 
	 * @return The complete URI
	 * 
	 * @throws URISyntaxException
	 */
	public URI createUri() throws URISyntaxException
	{
		if (baseUrl == null || baseUrl.length() == 0) {
			throw new SimpleHttpException("Cannot construct URI without base URL");
		}
		URIBuilder ub = new URIBuilder(baseUrl);
		if (path != null && path.length() != 0) {
			String basePath = ub.getPath();
			if (basePath == null) {
				ub.setPath(path);
			}
			else {
				if (basePath.charAt(basePath.length() - 1) != '/' && path.charAt(0) != '/')
					ub.setPath(basePath + '/' + path);
				else
					ub.setPath(basePath + path);
			}
		}
		if (queryParams != null) {
			for (String[] param : queryParams) {
				ub.addParameter(param[0], param[1]);
			}
		}
		else if (query != null) {
			ub.setCustomQuery(query);
		}
		return ub.build();
	}

	public void done()
	{
		if (request != null) {
			request.reset();
		}
	}

	/**
	 * Closes the Apache HttpClient underlying this {@code SimpleHttpRequest}
	 * instance.
	 */
	public void shutdown()
	{
		if (httpClient != null && httpClient instanceof CloseableHttpClient) {
			try {
				((CloseableHttpClient) httpClient).close();
			}
			catch (IOException e) {
				throw new SimpleHttpException(e);
			}
			finally {
				httpClient = null;
			}
		}
		if (sharedHttpClient != null && sharedHttpClient instanceof CloseableHttpClient) {
			try {
				((CloseableHttpClient) sharedHttpClient).close();
			}
			catch (IOException e) {
				throw new SimpleHttpException(e);
			}
			finally {
				sharedHttpClient = null;
			}
		}
	}

	abstract HttpRequestBase createRequest();

	HttpClient getHttpClient()
	{
		if (shareHttpClient) {
			if (httpClient == null) {
				httpClient = HttpClientBuilder.create().build();
			}
			return httpClient;
		}
		if (sharedHttpClient == null) {
			sharedHttpClient = HttpClientBuilder.create().build();
		}
		return sharedHttpClient;
	}

	private void checkResponse()
	{
		if (httpResponse == null) {
			throw new IllegalStateException("No response generated yet");
		}
	}

}
