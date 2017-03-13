package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;

public abstract class TaxonomicIdentification implements INbaModelObject {

	private String taxonRank;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String scientificNameGroup;
	private ScientificName scientificName;
	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;
	private List<VernacularName> vernacularNames;
	private List<String> identificationQualifiers;
	private Date dateIdentified;
	private List<Agent> identifiers;

	public void addIdentifier(Agent identifier)
	{
		if (identifiers == null) {
			identifiers = new ArrayList<Agent>(4);
		}
		identifiers.add(identifier);
	}

	public String getScientificNameGroup()
	{
		return scientificNameGroup;
	}

	public void setScientificNameGroup(String scientificNameGroup)
	{
		this.scientificNameGroup = scientificNameGroup;
	}

	public String getTaxonRank()
	{
		return taxonRank;
	}

	public void setTaxonRank(String taxonRank)
	{
		this.taxonRank = taxonRank;
	}

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

	public Date getDateIdentified()
	{
		return dateIdentified;
	}

	public void setDateIdentified(Date dateIdentified)
	{
		this.dateIdentified = dateIdentified;
	}

	public List<Agent> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(List<Agent> identifiers)
	{
		this.identifiers = identifiers;
	}

}
