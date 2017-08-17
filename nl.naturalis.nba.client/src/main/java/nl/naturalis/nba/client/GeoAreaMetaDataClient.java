package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.GeoArea;

/**
 * Provides access to information about the NBA's {@link GeoArea} index.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 *
 */
public class GeoAreaMetaDataClient extends NbaDocumentMetaDataClient<GeoArea> {

	GeoAreaMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

}
