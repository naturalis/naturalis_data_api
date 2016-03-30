package nl.naturalis.nda.client;

/**
 * A {@code ClientConfig} instance encapsulates the information necessary to
 * connect to a particular NBA instance (e.g. NBA production or NBA test).
 * 
 * @author Ayco Holleman
 *
 */
public class ClientConfig {

	/*
	 * Currently the only thing that distinguishes one NBA instance from the
	 * other is its base URL
	 */
	public final String baseUrl;


	public ClientConfig(String baseUrl)
	{
		this.baseUrl = baseUrl;
	}


	public String getBaseUrl()
	{
		return baseUrl;
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
