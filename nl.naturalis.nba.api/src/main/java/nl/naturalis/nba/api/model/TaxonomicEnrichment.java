package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Set;
import java.util.TreeSet;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;

/**
 * The {@code TaxonomicEnrichment} object enriches {@link Specimen} documents
 * with data extracted from the {@link Taxon} index. This allows you to search
 * for specimens by specifying taxonomic attributes.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicEnrichment implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private Set<String> vernacularNames;
	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private Set<String> synonyms;

	@NotIndexed
	private String scientificNameGroup;
	@NotIndexed
	private String taxonId;
	@NotIndexed
	private String sourceSystem;

	public TaxonomicEnrichment()
	{
	}

	public void addVernacularName(String vernacularName)
	{
		if (this.vernacularNames == null)
			this.vernacularNames = new TreeSet<>();
		this.vernacularNames.add(vernacularName);
	}

	public void addSynonym(String synonym)
	{
		if (this.synonyms == null)
			this.synonyms = new TreeSet<>();
		this.synonyms.add(synonym);
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

	public String getScientificNameGroup()
	{
		return scientificNameGroup;
	}

	public void setScientificNameGroup(String scientificNameGroup)
	{
		this.scientificNameGroup = scientificNameGroup;
	}

	/**
	 * Returns the ID of the taxon document from which the enrichments were
	 * extracted.
	 * 
	 * @return
	 */
	public String getTaxonId()
	{
		return taxonId;
	}

	/**
	 * Sets the ID of the taxon document from which the enrichments were
	 * extracted.
	 * 
	 * @param taxonId
	 */
	public void setTaxonId(String taxonId)
	{
		this.taxonId = taxonId;
	}

	/**
	 * Returns the {@link SourceSystem#getCode() source system code} of the
	 * {@link Taxon} (COL or NSR).
	 * 
	 * @return
	 */
	public String getSourceSystem()
	{
		return sourceSystem;
	}

	/**
	 * Sets the {@link SourceSystem#getCode() source system code} of the
	 * {@link Taxon} (COL or NSR).
	 * 
	 * @param sourceSystem
	 */
	public void setSourceSystem(String sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}

}
