package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Specimen;

/**
 * Provides access to information about the NBA's {@link Specimen} index.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenMetaDataClient extends NbaDocumentMetaDataClient<Specimen> {

	SpecimenMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

}
