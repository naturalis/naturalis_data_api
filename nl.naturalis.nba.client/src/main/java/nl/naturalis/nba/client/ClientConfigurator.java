package nl.naturalis.nba.client;

public class ClientConfigurator {

	private final ClientConfig cfg;

	public ClientConfigurator()
	{
		this.cfg = new ClientConfig();
	}

	public ClientConfigurator setBaseUrl(String baseUrl)
	{
		cfg.setBaseUrl(baseUrl);
		return this;
	}

	public NBA create()
	{
		return new NBA(cfg);
	}

}
