package nl.naturalis.nda.domain;

import java.util.List;

public class Taxon extends NdaTraceableObject {

	private String taxonRank;
	private ScientificName acceptedName;

	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;

	private List<Synonym> synonyms;
	private List<VernacularName> vernacularNames;
	private List<TaxonDescription> descriptions;
	private List<Specimen> specimens;


	public String getTaxonRank()
	{
		return taxonRank;
	}


	public void setTaxonRank(String taxonRank)
	{
		this.taxonRank = taxonRank;
	}


	/**
	 * Botanical
	 * 
	 * @return
	 */
	public ScientificName getAcceptedName()
	{
		return acceptedName;
	}


	/**
	 * Zoological
	 * 
	 * @return
	 */
	public ScientificName getValidName()
	{
		return acceptedName;
	}


	/**
	 * Botanical
	 */
	public void setAcceptedName(ScientificName scientificName)
	{
		this.acceptedName = scientificName;
	}


	/**
	 * Zoological
	 */
	public void setValidName(ScientificName scientificName)
	{
		this.acceptedName = scientificName;
	}


	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}


	public void setDefaultClassification(DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}


	/**
	 * Get the system classification of this taxon, i.e. the as-is
	 * classification of the source system.
	 * 
	 * @return The system classification of this taxon
	 */
	public List<Monomial> getSystemClassification()
	{
		return systemClassification;
	}


	public void setSystemClassification(List<Monomial> systemClassification)
	{
		this.systemClassification = systemClassification;
	}


	public List<Synonym> getSynonyms()
	{
		return synonyms;
	}


	public void setSynonyms(List<Synonym> synonyms)
	{
		this.synonyms = synonyms;
	}


	public List<VernacularName> getCommonNames()
	{
		return vernacularNames;
	}


	public void setCommonNames(List<VernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}


	public List<TaxonDescription> getDescriptions()
	{
		return descriptions;
	}


	public void setDescriptions(List<TaxonDescription> descriptions)
	{
		this.descriptions = descriptions;
	}


	public List<Specimen> getSpecimens()
	{
		return specimens;
	}


	public void setSpecimens(List<Specimen> specimens)
	{
		this.specimens = specimens;
	}

}
