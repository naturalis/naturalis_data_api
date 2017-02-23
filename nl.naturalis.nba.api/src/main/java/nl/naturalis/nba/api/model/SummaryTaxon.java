package nl.naturalis.nba.api.model;

import java.util.List;

/**
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryTaxon implements INbaModelObject {

	private String id;
	private SourceSystem sourceSystem;
	private SummaryScientificName acceptedName;
	private DefaultClassification defaultClassification;

	private List<Monomial> systemClassification;
	private List<SummaryScientificName> synonyms;
	private List<SummaryVernacularName> vernacularNames;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public SourceSystem getSourceSystem()
	{
		return sourceSystem;
	}

	public void setSourceSystem(SourceSystem sourceSystem)
	{
		this.sourceSystem = sourceSystem;
	}

	public SummaryScientificName getAcceptedName()
	{
		return acceptedName;
	}

	public void setAcceptedName(SummaryScientificName acceptedName)
	{
		this.acceptedName = acceptedName;
	}

	public DefaultClassification getDefaultClassification()
	{
		return defaultClassification;
	}

	public void setDefaultClassification(DefaultClassification defaultClassification)
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

	public List<SummaryScientificName> getSynonyms()
	{
		return synonyms;
	}

	public void setSynonyms(List<SummaryScientificName> synonyms)
	{
		this.synonyms = synonyms;
	}

	public List<SummaryVernacularName> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<SummaryVernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}
}
