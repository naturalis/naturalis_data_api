package nl.naturalis.nda.domain;

import java.util.List;

public class Taxon extends NdaTraceableObject {

	private ScientificName acceptedName;
	private String taxonRank;

	private DefaultClassification defaultClassification;
	private List<Monomial> actualClassification;

	private List<Synonym> synonyms;
	private List<CommonName> commonNames;
	private List<TaxonDescription> descriptions;
	private List<SpecimenUnit> specimenUnits;


	public ScientificName getAcceptedName()
	{
		return acceptedName;
	}


	public void setAcceptedName(ScientificName scientificName)
	{
		this.acceptedName = scientificName;
	}


	public String getTaxonRank()
	{
		return taxonRank;
	}


	public void setTaxonRank(String taxonRank)
	{
		this.taxonRank = taxonRank;
	}


	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}


	public void setDefaultClassification(DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}


	public List<Monomial> getActualClassification()
	{
		return actualClassification;
	}


	public void setActualClassification(List<Monomial> actualClassification)
	{
		this.actualClassification = actualClassification;
	}


	public List<Synonym> getSynonyms()
	{
		return synonyms;
	}


	public void setSynonyms(List<Synonym> synonyms)
	{
		this.synonyms = synonyms;
	}


	public List<CommonName> getCommonNames()
	{
		return commonNames;
	}


	public void setCommonNames(List<CommonName> commonNames)
	{
		this.commonNames = commonNames;
	}


	public List<TaxonDescription> getDescriptions()
	{
		return descriptions;
	}


	public void setDescriptions(List<TaxonDescription> descriptions)
	{
		this.descriptions = descriptions;
	}


	public List<SpecimenUnit> getOccurrences()
	{
		return specimenUnits;
	}


	public void setOccurrences(List<SpecimenUnit> specimenUnits)
	{
		this.specimenUnits = specimenUnits;
	}

}
