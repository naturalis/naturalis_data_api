package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import nl.naturalis.nba.api.model.ScientificNameGroup_old;

public class ScientificNameGroupMetaDataDao extends DocumentMetaDataDao<ScientificNameGroup_old> {

	public ScientificNameGroupMetaDataDao()
	{
		super(SCIENTIFIC_NAME_GROUP);
	}

}
