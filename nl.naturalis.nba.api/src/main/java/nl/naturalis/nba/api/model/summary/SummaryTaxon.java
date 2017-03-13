package nl.naturalis.nba.api.model.summary;

import java.util.List;

import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Monomial;

/**
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryTaxon implements INbaModelObject {

	@NotIndexed
	private String id;
	private SummarySourceSystem sourceSystem;
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

	public SummarySourceSystem getSourceSystem()
	{
		return sourceSystem;
	}

	public void setSourceSystem(SummarySourceSystem sourceSystem)
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
