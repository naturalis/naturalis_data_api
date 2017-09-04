package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;

public class MultiMediaObjectMetaDataDao extends NbaDocumentMetaDataDao<MultiMediaObject> {

	public MultiMediaObjectMetaDataDao()
	{
		super(MULTI_MEDIA_OBJECT);
	}

}
