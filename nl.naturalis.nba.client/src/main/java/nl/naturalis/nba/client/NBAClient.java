package nl.naturalis.nba.client;

import nl.naturalis.nba.api.IMultiMediaObjectAPI;
import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.ITaxonAPI;

public class NBAClient {

	private final ClientConfig cfg;

	NBAClient(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	public ISpecimenAPI getSpecimenAPI()
	{
		return new SpecimenClient(cfg);
	}

	public IMultiMediaObjectAPI getMultiMediaObjectAPI()
	{
		return new MultiMediaClient(cfg);
	}

	public ITaxonAPI getTaxonAPI()
	{
		return new TaxonClient(cfg);
	}

}
