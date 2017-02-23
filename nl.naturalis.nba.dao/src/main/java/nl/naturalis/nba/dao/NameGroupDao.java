package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.INameGroupAccess;
import nl.naturalis.nba.api.model.NameGroup;

public class NameGroupDao extends NbaDao<NameGroup> implements INameGroupAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(NameGroupDao.class);

	public NameGroupDao()
	{
		super(NAME_GROUP);
	}

	@Override
	NameGroup[] createDocumentObjectArray(int length)
	{
		return new NameGroup[length];
	}

}
