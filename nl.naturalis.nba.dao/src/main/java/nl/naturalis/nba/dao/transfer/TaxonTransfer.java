package nl.naturalis.nba.dao.transfer;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.types.ESTaxon;

public class TaxonTransfer {

	private TaxonTransfer()
	{
		// Only static method in transfer objects
	}

	public static Taxon transfer(ESTaxon in)
	{
		Taxon out = new Taxon();
		out.setSourceSystem(in.getSourceSystem());
		out.setSourceSystemId(in.getSourceSystemId());
		out.setSourceSystemParentId(in.getSourceSystemParentId());
		out.setRecordURI(in.getRecordURI());
		out.setTaxonRank(in.getTaxonRank());
		out.setTaxonRemarks(in.getTaxonRemarks());
		out.setOccurrenceStatusVerbatim(in.getOccurrenceStatusVerbatim());
		out.setAcceptedName(in.getAcceptedName());
		out.setDefaultClassification(in.getDefaultClassification());
		out.setSystemClassification(in.getSystemClassification());
		out.setSynonyms(in.getSynonyms());
		out.setVernacularNames(in.getVernacularNames());
		out.setDescriptions(in.getDescriptions());
		out.setReferences(in.getReferences());
		out.setExperts(in.getExperts());
		return out;
	}
}
