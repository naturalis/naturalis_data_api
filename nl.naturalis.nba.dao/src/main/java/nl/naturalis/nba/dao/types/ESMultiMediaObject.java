package nl.naturalis.nba.dao.types;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject.Type;
import nl.naturalis.nba.api.model.NbaTraceableObject;
import nl.naturalis.nba.api.model.ServiceAccessPoint;

@Deprecated
public class ESMultiMediaObject extends NbaTraceableObject implements ESType {

	private String sourceInstitutionID;
	private String sourceID;
	private String owner;
	private String licenceType;
	private String licence;
	private String unitID;
	private String collectionType;
	private String title;
	private String caption;
	private String description;
	private List<ServiceAccessPoint> serviceAccessPoints;
	private Type type;
	private int taxonCount;
	private String creator;
	private String copyrightText;
	private String associatedSpecimenReference;
	private String associatedTaxonReference;
	private String specimenTypeStatus;
	private boolean multiMediaPublic;
	private List<String> subjectParts;
	private List<String> subjectOrientations;
	private List<String> phasesOrStages;
	private List<String> sexes;
	private List<MultiMediaGatheringEvent> gatheringEvents;
	private List<MultiMediaContentIdentification> identifications;
	private List<String> identifyingEpithets;
	private List<String> theme;

	public void addServiceAccessPoint(ServiceAccessPoint sap)
	{
		if (serviceAccessPoints == null) {
			serviceAccessPoints = new ArrayList<ServiceAccessPoint>(4);
		}
		serviceAccessPoints.add(sap);
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

	public String getLicenceType()
	{
		return licenceType;
	}

	public void setLicenceType(String licenceType)
	{
		this.licenceType = licenceType;
	}

	public String getLicence()
	{
		return licence;
	}

	public void setLicence(String licence)
	{
		this.licence = licence;
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

	public String getSpecimenTypeStatus()
	{
		return specimenTypeStatus;
	}

	public void setSpecimenTypeStatus(String specimenTypeStatus)
	{
		this.specimenTypeStatus = specimenTypeStatus;
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

	public List<String> getIdentifyingEpithets()
	{
		return identifyingEpithets;
	}

	public List<String> getTheme()
	{
		return theme;
	}

	public void setTheme(List<String> theme)
	{
		this.theme = theme;
	}

}
