package nl.naturalis.nba.client;

/**
 * Java bean containing the configuration for how to connect to, and interact
 * with the NBA.
 * 
 * @author Ayco Holleman
 *
 */
public class ClientConfig {

	static final String PRODUCTION_BASE_URL = "https://api.biodiversitydata.nl/v2";

	private String baseUrl;
	private boolean preferGET;

	public ClientConfig()
	{
		this.baseUrl = PRODUCTION_BASE_URL;
	}

	/**
	 * Returns the base URL of the NBA REST service. Default
	 * https://api.biodiversitydata.nl/v2.
	 * 
	 * @return
	 */
	public String getBaseUrl()
	{
		return baseUrl;
	}

	/**
	 * Returns the base URL of the NBA REST service. Default
	 * https://api.biodiversitydata.nl/v2.
	 * 
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}

	/**
	 * Whether or not to issue GET requests whenever possible.
	 * 
	 * @return
	 */
	public boolean isPreferGET()
	{
		return preferGET;
	}

	/**
	 * Whether or not to issue GET requests whenever possible. Some methods in
	 * the API are exposed through end points that can be accessed both through
	 * POST requests and through GET requests. Ordinarily the client will use
	 * the POST variant to make sure large request payloads (e.g. containing geo
	 * shapes) can be sent to the server. This method allows you to override
	 * this behaviour.
	 * 
	 * @param preferGET
	 */
	public void setPreferGET(boolean preferGET)
	{
		this.preferGET = preferGET;
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
		return baseUrl.equals(other.baseUrl) && preferGET == other.preferGET;
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + baseUrl.hashCode();
		hash = (hash * 31) + (preferGET ? 1 : 0);
		return hash;
	}

}
