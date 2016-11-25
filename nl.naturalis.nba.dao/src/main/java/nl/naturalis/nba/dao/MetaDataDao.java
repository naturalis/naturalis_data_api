package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.query.OperatorCheck;

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

	@Override
	public boolean isOperatorAllowed(String field, ComparisonOperator operator)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("isOperatorAllowed(\"{}\",\"{}\")", field, operator);
		}
		try {
			return OperatorCheck.isOperatorAllowed(field, operator, dt);
		}
		catch (NoSuchFieldException e) {
			throw new DaoException(e);
		}
	}

}
