package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

public class TaxonomicEnrichment implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private Set<String> vernacularNames;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private Set<String> synonyms;
	
	@NotIndexed
	private List<Integer> identifications;
	@NotIndexed
	private String taxonId;
	@NotIndexed
	private String taxonSourceSystem;

	public TaxonomicEnrichment()
	{
	}

	public void addVernacularNames(Collection<String> vernacularNames)
	{
		if (this.vernacularNames == null)
			this.vernacularNames = new HashSet<>();
		this.vernacularNames.addAll(vernacularNames);
	}

	public void addSynonyms(ArrayList<String> synonyms)
	{
		if (this.synonyms == null)
			this.synonyms = new HashSet<>();
		this.synonyms.addAll(synonyms);
	}

	public List<Integer> getIdentifications()
	{
		return identifications;
	}

	public void setIdentifications(List<Integer> identifications)
	{
		this.identifications = identifications;
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

	public Set<String> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(Set<String> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

	public Set<String> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(Set<String> synonyms)
	{
		this.synonyms = synonyms;
	}

}
