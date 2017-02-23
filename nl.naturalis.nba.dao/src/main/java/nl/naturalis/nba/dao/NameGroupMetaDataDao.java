package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;

import nl.naturalis.nba.api.model.NameGroup;

public class NameGroupMetaDataDao extends MetaDataDao<NameGroup> {

	public NameGroupMetaDataDao()
	{
		super(NAME_GROUP);
	}

}
