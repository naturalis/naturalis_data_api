package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import nl.naturalis.nba.api.model.Specimen;

public class SpecimenMetaDataDao extends MetaDataDao<Specimen> {

	public SpecimenMetaDataDao()
	{
		super(SPECIMEN);
	}

}
