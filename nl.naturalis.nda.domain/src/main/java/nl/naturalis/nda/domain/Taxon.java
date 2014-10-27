package nl.naturalis.nda.domain;

import java.util.List;

public class Taxon extends NdaTraceableObject {

	private String sourceSystemParentId;

	private String taxonRank;
	private ScientificName acceptedName;

	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;

	private List<ScientificName> synonyms;
	private List<VernacularName> vernacularNames;
	private List<TaxonDescription> descriptions;
	private List<Reference> references;
	private List<Expert> experts;

	private List<Specimen> specimens;


	public String getSourceSystemParentId()
	{
		return sourceSystemParentId;
	}


	public void setSourceSystemParentId(String sourceSystemParentId)
	{
		this.sourceSystemParentId = sourceSystemParentId;
	}


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


	public List<ScientificName> getSynonyms()
	{
		return synonyms;
	}


	public void setSynonyms(List<ScientificName> synonyms)
	{
		this.synonyms = synonyms;
	}


	public List<VernacularName> getVernacularNames()
	{
		return vernacularNames;
	}


	public void setVernacularNames(List<VernacularName> vernacularNames)
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


	public List<Expert> getExperts()
	{
		return experts;
	}


	public void setExperts(List<Expert> experts)
	{
		this.experts = experts;
	}


	public List<Reference> getReferences()
	{
		return references;
	}


	public void setReferences(List<Reference> references)
	{
		this.references = references;
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
