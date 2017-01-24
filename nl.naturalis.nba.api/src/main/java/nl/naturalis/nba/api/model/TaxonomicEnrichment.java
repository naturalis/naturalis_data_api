package nl.naturalis.nba.api.model;

import java.util.List;
import java.util.Set;

public class TaxonomicEnrichment implements INbaModelObject {

	private Set<Integer> identificationArrayIndex;
	private String taxonId;
	private String taxonSourceSystem;
	private List<String> vernacularNames;
	private List<String> synonyms;

	public TaxonomicEnrichment()
	{
	}

	public Set<Integer> getIdentificationArrayIndex()
	{
		return identificationArrayIndex;
	}

	public void setIdentificationArrayIndex(Set<Integer> identificationArrayIndex)
	{
		this.identificationArrayIndex = identificationArrayIndex;
	}

	public String getTaxonId()
	{
		return taxonId;
	}

	public void setTaxonId(String taxonId)
	{
		this.taxonId = taxonId;
	}

	public String getTaxonSourceSystem()
	{
		return taxonSourceSystem;
	}

	public void setTaxonSourceSystem(String taxonSourceSystem)
	{
		this.taxonSourceSystem = taxonSourceSystem;
	}

	public List<String> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<String> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

	public List<String> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(List<String> synonyms)
	{
		this.synonyms = synonyms;
	}

}
