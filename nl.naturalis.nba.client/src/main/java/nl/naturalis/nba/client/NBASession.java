package nl.naturalis.nba.client;

public class NBASession {

	private final ClientConfig cfg;

	NBASession(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	public SpecimenClient getSpecimenClient()
	{
		return new SpecimenClient(cfg);
	}

	public MultiMediaClient getMultiMediaObjectClient()
	{
		return new MultiMediaClient(cfg);
	}

	public TaxonClient getTaxonClient()
	{
		return new TaxonClient(cfg);
	}

}
