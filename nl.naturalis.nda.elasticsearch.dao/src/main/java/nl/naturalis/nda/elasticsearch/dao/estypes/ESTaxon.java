package nl.naturalis.nda.elasticsearch.dao.estypes;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.DefaultClassification;
import nl.naturalis.nda.domain.Monomial;
import nl.naturalis.nda.domain.NdaTraceableObject;
import nl.naturalis.nda.domain.ScientificName;
import nl.naturalis.nda.domain.TaxonDescription;
import nl.naturalis.nda.domain.VernacularName;

public class ESTaxon extends NdaTraceableObject {

	private String sourceSystemParentId;

	private String taxonRank;
	private ScientificName acceptedName;

	private DefaultClassification defaultClassification;

	private List<ScientificName> synonyms;
	private List<VernacularName> vernacularNames;
	private List<Monomial> monomials;
	private List<TaxonDescription> descriptions;


	public void addSynonym(ScientificName synonym)
	{
		if (synonyms == null) {
			synonyms = new ArrayList<ScientificName>();
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


	public void addVernacularName(VernacularName name)
	{
		if (vernacularNames == null) {
			vernacularNames = new ArrayList<VernacularName>();
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
