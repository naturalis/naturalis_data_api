package nl.naturalis.nba.client;

/**
 * A Java bean containing the information necessary to connect to a particular
 * NBA instance (e.g. NBA production or NBA test).
 * 
 * @author Ayco Holleman
 *
 */
public class ClientConfig {

	static final String PRODUCTION_BASE_URL = "http://api.biodiversitydata.nl/v2";

	private String baseUrl;

	public ClientConfig(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	public ClientConfig()
	{
		this.baseUrl = PRODUCTION_BASE_URL;
	}

	public String getBaseUrl()
	{
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj.getClass() == ClientConfig.class)) {
			return false;
		}
		ClientConfig other = (ClientConfig) obj;
		return baseUrl.equals(other.baseUrl);
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + baseUrl.hashCode();
		return hash;
	}

}
