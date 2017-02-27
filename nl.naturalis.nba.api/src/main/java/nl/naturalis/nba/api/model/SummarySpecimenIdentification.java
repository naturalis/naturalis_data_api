package nl.naturalis.nba.api.model;

import java.util.List;

public class SummarySpecimenIdentification implements INbaModelObject {

	private SummaryScientificName scientificName;
	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;
	private List<SummaryVernacularName> vernacularNames;

	public SummaryScientificName getScientificName()
	{
		return scientificName;
	}

	public void setScientificName(SummaryScientificName scientificName)
	{
		this.scientificName = scientificName;
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

	public List<SummaryVernacularName> getVernacularNames()
	{
		return vernacularNames;
	}

	public void setVernacularNames(List<SummaryVernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}

}
