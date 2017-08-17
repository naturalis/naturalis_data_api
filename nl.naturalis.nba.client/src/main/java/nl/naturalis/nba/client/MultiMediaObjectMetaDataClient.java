package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.MultiMediaObject;

/**
 * Provides access to information about the NBA's {@link MultiMediaObject} index.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 *
 */
public class MultiMediaObjectMetaDataClient extends NbaDocumentMetaDataClient<MultiMediaObject> {

	MultiMediaObjectMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

}
