package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import nl.naturalis.nba.api.model.ScientificNameGroup;

public class ScientificNameGroupMetaDataDao extends DocumentMetaDataDao<ScientificNameGroup> {

	public ScientificNameGroupMetaDataDao()
	{
		super(SCIENTIFIC_NAME_GROUP);
	}

}
