package nl.naturalis.nba.client;

public class NBASessionConfigurator {

	private final ClientConfig cfg;

	public NBASessionConfigurator()
	{
		this.cfg = new ClientConfig();
	}

	public NBASessionConfigurator setBaseUrl(String baseUrl)
	{
		cfg.setBaseUrl(baseUrl);
		return this;
	}

	public NBASession create()
	{
		return new NBASession(cfg);
	}

}
