package nl.naturalis.nba.api.model;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySourceSystem;
import nl.naturalis.nba.api.model.summary.SummaryVernacularName;

/**
 * The {@code TaxonomicEnrichment} object enriches {@link Specimen} documents
 * with data extracted from the {@link Taxon} index. This allows you to search
 * for specimens by specifying taxonomic attributes.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonomicEnrichment implements INbaModelObject {

	private List<SummaryVernacularName> vernacularNames;
	private List<SummaryScientificName> synonyms;
	private DefaultClassification defaultClassification;

	private SummarySourceSystem sourceSystem;
	@NotIndexed
	private String taxonId;

	public TaxonomicEnrichment()
	{
	}

	public void addVernacularName(SummaryVernacularName vernacularName)
	{
		if (this.vernacularNames == null)
			this.vernacularNames = new ArrayList<>(5);
		this.vernacularNames.add(vernacularName);
	}

	public void addSynonym(SummaryScientificName synonym)
	{
		if (this.synonyms == null)
			this.synonyms = new ArrayList<>(5);
		this.synonyms.add(synonym);
	}

	public List<SummaryVernacularName> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<SummaryVernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

	public List<SummaryScientificName> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(List<SummaryScientificName> synonyms)
	{
		this.synonyms = synonyms;
	}

	public void setDefaultClassification(DefaultClassification defaultClassification) {
	  this.defaultClassification = defaultClassification;
	}
	
	public DefaultClassification getDefaultClassification() {
	  return defaultClassification;
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
	public SummarySourceSystem getSourceSystem()
	{
		return sourceSystem;
	}

	/**
	 * Sets the {@link SourceSystem#getCode() source system code} of the
	 * {@link Taxon} (COL or NSR).
	 * 
	 * @param sourceSystem
	 */
	public void setSourceSystem(SummarySourceSystem sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}
	

}
