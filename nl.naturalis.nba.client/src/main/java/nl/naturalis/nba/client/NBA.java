package nl.naturalis.nba.client;

public class NBA {

	private final ClientConfig cfg;

	NBA(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	public SpecimenClient getSpecimenAPI()
	{
		return new SpecimenClient(cfg);
	}

	public MultiMediaClient getMultiMediaObjectAPI()
	{
		return new MultiMediaClient(cfg);
	}

	public TaxonClient getTaxonAPI()
	{
		return new TaxonClient(cfg);
	}

}
