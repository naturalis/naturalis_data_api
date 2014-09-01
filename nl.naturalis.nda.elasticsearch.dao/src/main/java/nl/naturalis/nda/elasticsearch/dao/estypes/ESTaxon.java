package nl.naturalis.nda.elasticsearch.dao.estypes;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.NdaTraceableObject;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonDescription;

public class ESTaxon extends NdaTraceableObject {

	private String sourceSystemParentId;

	private ScientificName acceptedName;
	private String taxonRank;

	private DefaultClassification defaultClassification;

	private List<String> synonyms;
	private List<String> vernacularNames;
	private List<Monomial> monomials;
	private List<TaxonDescription> descriptions;


	public void addSynonym(String synonym)
	{
		if (synonyms == null) {
			synonyms = new ArrayList<String>();
		}
		synonyms.add(synonym);
	}


	public void addMonomial(Monomial monomial)
	{
		if (monomials == null) {
			monomials = new ArrayList<Monomial>();
		}
		monomials.add(monomial);
	}


	public void addVernacularName(String name)
	{
		if (vernacularNames == null) {
			vernacularNames = new ArrayList<String>();
		}
		vernacularNames.add(name);
	}


	public void addDescription(TaxonDescription description)
	{
		if (descriptions == null) {
			descriptions = new ArrayList<TaxonDescription>();
		}
		descriptions.add(description);
	}


	public String getSourceSystemParentId()
	{
		return sourceSystemParentId;
	}


	public void setSourceSystemParentId(String sourceSystemParentId)
	{
		this.sourceSystemParentId = sourceSystemParentId;
	}


	public ScientificName getAcceptedName()
	{
		return acceptedName;
	}


	public void setAcceptedName(ScientificName acceptedName)
	{
		this.acceptedName = acceptedName;
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


	public List<String> getSynonyms()
	{
		return synonyms;
	}


	public void setSynonyms(List<String> synonyms)
	{
		this.synonyms = synonyms;
	}


	public List<String> getVernacularNames()
	{
		return vernacularNames;
	}


	public void setVernacularNames(List<String> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}


	public List<Monomial> getMonomials()
	{
		return monomials;
	}


	public void setMonomials(List<Monomial> monomials)
	{
		this.monomials = monomials;
	}


	public List<TaxonDescription> getDescriptions()
	{
		return descriptions;
	}


	public void setDescriptions(List<TaxonDescription> descriptions)
	{
		this.descriptions = descriptions;
	}

}
