package nl.naturalis.nba.dao.transfer;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.types.ESTaxon;

public class TaxonTransfer {

	private TaxonTransfer()
	{
		// Only static method in transfer objects
	}

	public static Taxon transfer(ESTaxon esTaxon)
	{
		Taxon taxon = new Taxon();
		taxon.setSourceSystem(esTaxon.getSourceSystem());
		taxon.setSourceSystemId(esTaxon.getSourceSystemId());
		taxon.setSourceSystemParentId(esTaxon.getSourceSystemParentId());
		taxon.setRecordURI(esTaxon.getRecordURI());
		taxon.setTaxonRank(esTaxon.getTaxonRank());
		taxon.setTaxonRemarks(esTaxon.getTaxonRemarks());
		taxon.setOccurrenceStatusVerbatim(esTaxon.getOccurrenceStatusVerbatim());
		taxon.setAcceptedName(esTaxon.getAcceptedName());
		taxon.setDefaultClassification(esTaxon.getDefaultClassification());
		taxon.setSystemClassification(esTaxon.getSystemClassification());
		taxon.setSynonyms(esTaxon.getSynonyms());
		taxon.setVernacularNames(esTaxon.getVernacularNames());
		taxon.setDescriptions(esTaxon.getDescriptions());
		taxon.setReferences(esTaxon.getReferences());
		taxon.setExperts(esTaxon.getExperts());
		return taxon;
	}
}