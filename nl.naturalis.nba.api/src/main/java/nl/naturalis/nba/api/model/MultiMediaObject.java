package nl.naturalis.nba.api.model;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotStored;
import nl.naturalis.nba.api.model.Type;

/**
 * 
 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dwc:scientificName
 */
public class MultiMediaObject extends NbaTraceableObject implements IDocumentObject {

//	/**
//	 * Enumeration of the possible types of a {@code Media} object.
//	 * 
//	 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dc:type
//	 */
//	public static enum Type
//	{
//		COLLECTION, STILL_IMAGE, SOUND, MOVING_IMAGE, INTERACTIVE_RESOURCE, TEXT, OTHER
//	}

	@NotStored
	private String id;
	private String sourceInstitutionID;
	private String sourceID;
	@SuppressWarnings("unused")
  private List<String> previousSourceID;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String owner;
	private String licenseType;
	private String license;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String unitID;
	@SuppressWarnings("unused")
  private String previousUnitsText;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String collectionType;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String title;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String caption;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String description;
	private List<ServiceAccessPoint> serviceAccessPoints;
	private Type type;
	private int taxonCount;
	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String creator;
	private String copyrightText;
	private String associatedSpecimenReference;
	private String associatedTaxonReference;
	private boolean multiMediaPublic;
	private List<String> subjectParts;
	private List<String> subjectOrientations;
	private List<String> phasesOrStages;
	private List<String> sexes;
	private List<MultiMediaGatheringEvent> gatheringEvents;
	private List<MultiMediaContentIdentification> identifications;
	private List<String> theme;

	// Non-persistent data
	@NotStored
	private Specimen associatedSpecimen;
	@NotStored
	private Taxon associatedTaxon;

	public void addServiceAccessPoint(String uri, String format, ServiceAccessPoint.Variant variant)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>();
		}
		serviceAccessPoints.add(new ServiceAccessPoint(uri, format, variant));
	}

	public void addServiceAccessPoint(ServiceAccessPoint sap)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>();
		}
		serviceAccessPoints.add(sap);
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

	public String getSourceInstitutionID()
	{
		return sourceInstitutionID;
	}

	public void setSourceInstitutionID(String sourceInstitutionID)
	{
		this.sourceInstitutionID = sourceInstitutionID;
	}

	public String getSourceID()
	{
		return sourceID;
	}

	public void setSourceID(String sourceID)
	{
		this.sourceID = sourceID;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public String getLicenseType()
	{
		return licenseType;
	}

	public void setLicenseType(String licenseType)
	{
		this.licenseType = licenseType;
	}

	public String getLicense()
	{
		return license;
	}

	public void setLicense(String license)
	{
		this.license = license;
	}

	public String getUnitID()
	{
		return unitID;
	}

	public void setUnitID(String unitID)
	{
		this.unitID = unitID;
	}

	public String getCollectionType()
	{
		return collectionType;
	}

	public void setCollectionType(String collectionType)
	{
		this.collectionType = collectionType;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getCaption()
	{
		return caption;
	}

	public void setCaption(String caption)
	{
		this.caption = caption;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<ServiceAccessPoint> getServiceAccessPoints()
	{
		return serviceAccessPoints;
	}

	public void setServiceAccessPoints(List<ServiceAccessPoint> serviceAccessPoints)
	{
		this.serviceAccessPoints = serviceAccessPoints;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public int getTaxonCount()
	{
		return taxonCount;
	}

	public void setTaxonCount(int taxonCount)
	{
		this.taxonCount = taxonCount;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getCopyrightText()
	{
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText)
	{
		this.copyrightText = copyrightText;
	}

	public String getAssociatedSpecimenReference()
	{
		return associatedSpecimenReference;
	}

	public void setAssociatedSpecimenReference(String associatedSpecimenReference)
	{
		this.associatedSpecimenReference = associatedSpecimenReference;
	}

	public String getAssociatedTaxonReference()
	{
		return associatedTaxonReference;
	}

	public void setAssociatedTaxonReference(String associatedTaxonReference)
	{
		this.associatedTaxonReference = associatedTaxonReference;
	}

	public boolean isMultiMediaPublic()
	{
		return multiMediaPublic;
	}

	public void setMultiMediaPublic(boolean multiMediaPublic)
	{
		this.multiMediaPublic = multiMediaPublic;
	}

	public List<String> getSubjectParts()
	{
		return subjectParts;
	}

	public void setSubjectParts(List<String> subjectParts)
	{
		this.subjectParts = subjectParts;
	}

	public List<String> getSubjectOrientations()
	{
		return subjectOrientations;
	}

	public void setSubjectOrientations(List<String> subjectOrientations)
	{
		this.subjectOrientations = subjectOrientations;
	}

	public List<String> getPhasesOrStages()
	{
		return phasesOrStages;
	}

	public void setPhasesOrStages(List<String> phasesOrStages)
	{
		this.phasesOrStages = phasesOrStages;
	}

	public List<String> getSexes()
	{
		return sexes;
	}

	public void setSexes(List<String> sexes)
	{
		this.sexes = sexes;
	}

	public List<MultiMediaGatheringEvent> getGatheringEvents()
	{
		return gatheringEvents;
	}

	public void setGatheringEvents(List<MultiMediaGatheringEvent> gatheringEvents)
	{
		this.gatheringEvents = gatheringEvents;
	}

	public List<MultiMediaContentIdentification> getIdentifications()
	{
		return identifications;
	}

	public void setIdentifications(List<MultiMediaContentIdentification> identifications)
	{
		this.identifications = identifications;
	}

	public List<String> getTheme()
	{
		return theme;
	}

	public void setTheme(List<String> theme)
	{
		this.theme = theme;
	}

	public Specimen getAssociatedSpecimen()
	{
		return associatedSpecimen;
	}

	public void setAssociatedSpecimen(Specimen associatedSpecimen)
	{
		this.associatedSpecimen = associatedSpecimen;
	}

	public Taxon getAssociatedTaxon()
	{
		return associatedTaxon;
	}

	public void setAssociatedTaxon(Taxon associatedTaxon)
	{
		this.associatedTaxon = associatedTaxon;
	}

}
