package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;

abstract class MetaDataDao<T extends IDocumentObject> implements INbaMetaData<T> {

	private static final Logger logger = getLogger(MetaDataDao.class);

	private final DocumentType<T> dt;

	MetaDataDao(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	@Override
	public String getMapping()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getMapping()");
		}
		return JsonUtil.toPrettyJson(dt.getMapping());
	}

}
