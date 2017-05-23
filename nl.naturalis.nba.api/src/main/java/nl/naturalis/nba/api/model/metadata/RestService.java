package nl.naturalis.nba.api.model.metadata;

/**
 * A standard Java bean encapsulating info about one of the NBA's REST services.
 * 
 * @author Ayco Holleman
 *
 */
public class RestService {

	private String endPoint;
	private String method;
	private String consumes;
	private String produces;
	private String url;

	public String getEndPoint()
	{
		return endPoint;
	}

	public void setEndPoint(String endPoint)
	{
		this.endPoint = endPoint;
	}

	public String getMethod()
	{
		return method;
	}

	public void setMethod(String httpMethod)
	{
		this.method = httpMethod;
	}

	public String getConsumes()
	{
		return consumes;
	}

	public void setConsumes(String consumes)
	{
		this.consumes = consumes;
	}

	public String getProduces()
	{
		return produces;
	}

	public void setProduces(String produces)
	{
		this.produces = produces;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

}
