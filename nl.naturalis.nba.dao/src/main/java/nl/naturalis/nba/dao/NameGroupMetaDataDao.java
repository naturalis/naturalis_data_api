package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import nl.naturalis.nba.api.model.ScientificNameGroup;

public class NameGroupMetaDataDao extends MetaDataDao<ScientificNameGroup> {

	public NameGroupMetaDataDao()
	{
		super(SCIENTIFIC_NAME_GROUP);
	}

}
