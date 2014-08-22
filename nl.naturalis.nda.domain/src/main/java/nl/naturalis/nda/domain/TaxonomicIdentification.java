package nl.naturalis.nda.domain;

import java.util.Date;
import java.util.List;

public abstract class TaxonomicIdentification {

	private ScientificName scientificName;
	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;
	private List<VernacularName> vernacularNames;
	private List<String> identificationQualifiers;
	private String identifiedBy;
	private Date dateIdentified;


	public ScientificName getScientificName()
	{
		return scientificName;
	}


	public void setScientificName(ScientificName scientificName)
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


	public List<VernacularName> getVernacularNames()
	{
		return vernacularNames;
	}


	public void setVernacularNames(List<VernacularName> vernacularNames)
	{
		this.vernacularNames = vernacularNames;
	}


	public List<String> getIdentificationQualifiers()
	{
		return identificationQualifiers;
	}


	public void setIdentificationQualifiers(List<String> identificationQualifiers)
	{
		this.identificationQualifiers = identificationQualifiers;
	}


	public String getIdentifiedBy()
	{
		return identifiedBy;
	}


	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}


	public Date getDateIdentified()
	{
		return dateIdentified;
	}


	public void setDateIdentified(Date dateIdentified)
	{
		this.dateIdentified = dateIdentified;
	}
}
