package nl.naturalis.nda.client;

import java.util.HashMap;

/**
 * A {@code ClientFactory} is a factory for clients of a particular root
 * resource within the NBA (version, specimen, taxon, multimedia, ...). 
 * 
 * @author Ayco Holleman
 *
 */
public class ClientFactory {

	private static HashMap<ClientConfig, ClientFactory> factories = new HashMap<>(8);


	public static ClientFactory getInstance(ClientConfig cfg)
	{
		ClientFactory cf = factories.get(cfg);
		if (cf == null) {
			cf = new ClientFactory(cfg);
			factories.put(cfg, cf);
		}
		return cf;
	}


	public SpecimenClient createSpecimenClient()
	{
		if (specimentClient == null) {
			specimentClient = new SpecimenClient(cfg);
		}
		return specimentClient;
	}

	private final ClientConfig cfg;

	private SpecimenClient specimentClient;


	private ClientFactory(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

}
