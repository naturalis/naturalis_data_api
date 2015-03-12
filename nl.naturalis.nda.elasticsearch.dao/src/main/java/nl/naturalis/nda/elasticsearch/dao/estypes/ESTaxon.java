package nl.naturalis.nda.elasticsearch.dao.estypes;

import static nl.naturalis.nda.elasticsearch.dao.util.ESConstants.IDENTIFYING_EPITHETS_DELIMITER;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Expert;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.NdaTraceableObject;
import nl.naturalis.nda.domain.Reference;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.VernacularName;

public class ESTaxon extends NdaTraceableObject
{

	private String sourceSystemParentId;

	private String taxonRank;
	private ScientificName acceptedName;
	private String identifyingEpithets;

	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;

	private List<ScientificName> synonyms;
	private List<VernacularName> vernacularNames;
	private List<TaxonDescription> descriptions;
	private List<Reference> references;
	private List<Expert> experts;
	private List<String> localities;

	public void addSynonym(ScientificName synonym)
	{
		if (synonyms == null)
		{
			synonyms = new ArrayList<>();
		}
		synonyms.add(synonym);
	}

	public void addMonomial(Monomial monomial)
	{
		if (systemClassification == null)
		{
			systemClassification = new ArrayList<>();
		}
		systemClassification.add(monomial);
	}

	public void addVernacularName(VernacularName name)
	{
		if (vernacularNames == null)
		{
			vernacularNames = new ArrayList<>();
		}
		vernacularNames.add(name);
	}

	public void addReference(Reference reference)
	{
		if (references == null)
		{
			references = new ArrayList<>();
		}
		references.add(reference);
	}

	public void addDescription(TaxonDescription description)
	{
		if (descriptions == null)
		{
			descriptions = new ArrayList<>();
		}
		descriptions.add(description);
	}

	public void addLocality(String locality)
	{
		if (localities == null)
		{
			localities = new ArrayList<>();
		}
		localities.add(locality);
	}

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

	public ScientificName getAcceptedName()
	{
		return acceptedName;
	}

	public void setAcceptedName(ScientificName acceptedName)
	{
		this.acceptedName = acceptedName;
		// @formatter:off
		String identifier = acceptedName.getGenusOrMonomial()
				+ IDENTIFYING_EPITHETS_DELIMITER + acceptedName.getSubgenus()
				+ IDENTIFYING_EPITHETS_DELIMITER
				+ acceptedName.getSpecificEpithet()
				+ IDENTIFYING_EPITHETS_DELIMITER
				+ acceptedName.getInfraspecificEpithet();
		// @formatter:on
		this.identifyingEpithets = identifier;
	}

	public String getIdentifyingEpithets()
	{
		return identifyingEpithets;
	}

	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}

	public void setDefaultClassification(
			DefaultClassification defaultClassification)
	{
		this.defaultClassification = defaultClassification;
	}

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

	public List<Reference> getReferences()
	{
		return references;
	}

	public void setReferences(List<Reference> references)
	{
		this.references = references;
	}

	public List<Expert> getExperts()
	{
		return experts;
	}

	public void setExperts(List<Expert> experts)
	{
		this.experts = experts;
	}

	public List<String> getLocalities()
	{
		return localities;
	}

	public void setLocalities(List<String> localities)
	{
		this.localities = localities;
	}

}
