package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Taxon;

/**
 * Provides access to information about the NBA's {@link Taxon} index.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonMetaDataClient extends NbaDocumentMetaDataClient<Taxon> {

	TaxonMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

}
