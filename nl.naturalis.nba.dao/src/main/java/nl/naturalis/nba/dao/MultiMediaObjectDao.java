package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.model.MultiMediaObject;

public class MultiMediaObjectDao extends NbaDao<MultiMediaObject>
		implements IMultiMediaObjectAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(MultiMediaObjectDao.class);

	public MultiMediaObjectDao()
	{
		super(MULTI_MEDIA_OBJECT);
	}

	@Override
	MultiMediaObject[] createDocumentObjectArray(int length)
	{
		return new MultiMediaObject[length];
	}

}
