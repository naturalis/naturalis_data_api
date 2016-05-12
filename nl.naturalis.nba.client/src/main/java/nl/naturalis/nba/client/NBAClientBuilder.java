package nl.naturalis.nba.client;

public class NBAClientBuilder {

	private final ClientConfig cfg;

	public NBAClientBuilder()
	{
		this.cfg = new ClientConfig();
	}

	public NBAClientBuilder setBaseUrl(String baseUrl)
	{
		cfg.setBaseUrl(baseUrl);
		return this;
	}

	public NBAClient build()
	{
		return new NBAClient(cfg);
	}

}
