package nl.naturalis.nda.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dwc:scientificName
 */
public class MultiMediaObject extends NdaTraceableObject {

	/**
	 * Enumeration of the possible types of a {@code Media} object.
	 * 
	 * @see http://terms.tdwg.org/wiki/Audubon_Core_Term_List#dc:type
	 */
	public static enum Type
	{
		COLLECTION, STILL_IMAGE, SOUND, MOVING_IMAGE, INTERACTIVE_RESOURCE, TEXT, OTHER
	}

	private String sourceInstitutionID;
	private String sourceID;
	private String owner;
	private String licenseType;
	private String license;
	private String unitID;
	private String collectionType;
	private String title;
	private String caption;
	private String description;
	private Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints;
	private Type type;
	private int taxonCount;
	private String creator;
	private String copyrightText;
	private String associatedSpecimenReference;
	private String associatedTaxonReference;
	private SpecimenTypeStatus specimenTypeStatus;
	private boolean multimediaPublic;
	private List<String> subjectParts;
	private List<String> subjectOrientations;
	private List<String> phasesOrStages;
	private List<String> sexes;
	private List<MultiMediaGatheringEvent> gatheringEvents;
	private List<MultiMediaContentIdentification> identifications;

	// Non-persistent data
	private Specimen associatedSpecimen;
	private Taxon associatedTaxon;


	public void addServiceAccessPoint(String uri, String format, ServiceAccessPoint.Variant variant)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new HashMap<ServiceAccessPoint.Variant, ServiceAccessPoint>();
		}
		serviceAccessPoints.put(variant, new ServiceAccessPoint(uri, format, variant));
	}


	public void addServiceAccessPoint(ServiceAccessPoint sap)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new HashMap<ServiceAccessPoint.Variant, ServiceAccessPoint>();
		}
		serviceAccessPoints.put(sap.getVariant(), sap);
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


	public Map<ServiceAccessPoint.Variant, ServiceAccessPoint> getServiceAccessPoints()
	{
		return serviceAccessPoints;
	}


	public void setServiceAccessPoints(Map<ServiceAccessPoint.Variant, ServiceAccessPoint> serviceAccessPoints)
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


	public SpecimenTypeStatus getSpecimenTypeStatus()
	{
		return specimenTypeStatus;
	}


	public void setSpecimenTypeStatus(SpecimenTypeStatus specimenTypeStatus)
	{
		this.specimenTypeStatus = specimenTypeStatus;
	}


	public boolean isMultimediaPublic()
	{
		return multimediaPublic;
	}


	public void setMultimediaPublic(boolean multimediaPublic)
	{
		this.multimediaPublic = multimediaPublic;
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
