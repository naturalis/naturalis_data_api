package nl.naturalis.nda.client;

import nl.naturalis.nba.api.ISpecimenAPI;

public class NBAClient {

	private final ClientConfig cfg;

	public NBAClient(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	public ISpecimenAPI getSpecimenAPI()
	{
		return new SpecimenClient(cfg);
	}

}
