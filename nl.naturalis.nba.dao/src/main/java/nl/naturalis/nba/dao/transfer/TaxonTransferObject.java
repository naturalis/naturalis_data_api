package nl.naturalis.nba.dao.transfer;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.types.ESTaxon;

@Deprecated
public class TaxonTransferObject implements ITransferObject<Taxon, ESTaxon> {

	public TaxonTransferObject()
	{
	}

	@Override
	public Taxon getApiObject(ESTaxon in, String elasticsearchId)
	{
		Taxon out = new Taxon();
		out.setId(elasticsearchId);
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

	@Override
	public ESTaxon getEsObject(Taxon in)
	{
		ESTaxon out = new ESTaxon();
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
