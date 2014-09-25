package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

public class TaxonTransfer {

	private TaxonTransfer()
	{
		// Only static method in transfer objects
	}


	public static Taxon transfer(ESTaxon esTaxon)
	{
		Taxon taxon = new Taxon();
		taxon.setAcceptedName(esTaxon.getAcceptedName());
		taxon.setDefaultClassification(esTaxon.getDefaultClassification());
		taxon.setDescriptions(esTaxon.getDescriptions());
		taxon.setExperts(esTaxon.getExperts());
		taxon.setReferences(esTaxon.getReferences());
		taxon.setSourceSystem(esTaxon.getSourceSystem());
		taxon.setSourceSystemId(esTaxon.getSourceSystemId());
		taxon.setSynonyms(esTaxon.getSynonyms());
		taxon.setSystemClassification(esTaxon.getSystemClassification());
		taxon.setTaxonRank(esTaxon.getTaxonRank());
		taxon.setVernacularNames(esTaxon.getVernacularNames());
		return taxon;
	}

}
