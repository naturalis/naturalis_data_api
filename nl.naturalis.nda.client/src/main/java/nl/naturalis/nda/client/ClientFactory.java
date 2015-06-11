package nl.naturalis.nda.client;

import java.util.HashMap;

/**
 * @author Ayco Holleman
 *
 */
public class ClientFactory {

	private static HashMap<ClientConfig, SpecimenClient> specimenClients = new HashMap<>(8);


	public static SpecimenClient createSpecimenClient(ClientConfig cfg)
	{
		SpecimenClient client = specimenClients.get(cfg);
		if (client == null) {
			client = new SpecimenClient(cfg);
			specimenClients.put(cfg, client);
		}
		return client;
	}

	private final ClientConfig cfg;


	private ClientFactory(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

}
