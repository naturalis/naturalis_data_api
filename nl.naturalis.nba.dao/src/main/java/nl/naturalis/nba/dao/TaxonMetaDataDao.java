package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import nl.naturalis.nba.api.model.Taxon;

public class TaxonMetaDataDao extends NbaDocumentMetaDataDao<Taxon> {

	public TaxonMetaDataDao()
	{
		super(TAXON);
	}

}
