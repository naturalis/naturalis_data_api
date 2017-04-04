package nl.naturalis.nba.api.model.summary;

import java.util.List;

import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;

public class SummarySpecimenIdentification implements INbaModelObject {

	private SpecimenTypeStatus typeStatus;
	private SummaryScientificName scientificName;
	private DefaultClassification defaultClassification;
	private List<Monomial> systemClassification;
	private List<SummaryVernacularName> vernacularNames;
	private List<TaxonomicEnrichment> taxonomicEnrichments;

	public SpecimenTypeStatus getTypeStatus()
	{
		return typeStatus;
	}

	public void setTypeStatus(SpecimenTypeStatus typeStatus)
	{
		this.typeStatus = typeStatus;
	}

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

	public List<TaxonomicEnrichment> getTaxonomicEnrichments()
	{
		return taxonomicEnrichments;
	}

	public void setTaxonomicEnrichments(List<TaxonomicEnrichment> taxonomicEnrichments)
	{
		this.taxonomicEnrichments = taxonomicEnrichments;
	}

}
