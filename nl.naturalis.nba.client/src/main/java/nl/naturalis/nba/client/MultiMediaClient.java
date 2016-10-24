package nl.naturalis.nba.client;

import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.model.MultiMediaObject;

public class MultiMediaClient extends NbaClient<MultiMediaObject> implements IMultiMediaObjectAccess {

	MultiMediaClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

}
