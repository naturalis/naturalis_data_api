package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;

import nl.naturalis.nba.api.model.ScientificNameSummary;

public class ScientificNameSummaryMetaDataDao extends MetaDataDao<ScientificNameSummary> {

	public ScientificNameSummaryMetaDataDao()
	{
		super(SCIENTIFIC_NAME_SUMMARY);
	}

}
