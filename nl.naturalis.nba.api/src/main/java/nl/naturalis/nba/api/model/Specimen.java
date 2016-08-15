package nl.naturalis.nba.api.model;

import java.util.List;

public class Specimen extends NbaTraceableObject implements IDocumentObject {

	private String id;
	private String unitID;
	private String unitGUID;
	private String collectorsFieldNumber;
	private String assemblageID;
	private String sourceInstitutionID;
	private String sourceID;
	private String owner;
	private String licenseType;
	private String license;
	private String recordBasis;
	private String kindOfUnit;
	private String collectionType;
	private SpecimenTypeStatus typeStatus;
	private Sex sex;
	private PhaseOrStage phaseOrStage;
	private String title;
	private String notes;
	private String preparationType;
	private int numberOfSpecimen;
	private boolean fromCaptivity;
	private boolean objectPublic;
	private boolean multiMediaPublic;

	private Agent acquiredFrom;
	private GatheringEvent gatheringEvent;
	private List<SpecimenIdentification> identifications;
	private List<Specimen> otherSpecimensInAssemblage;
	private List<Taxon> associatedTaxa;

	public void addIndentification(SpecimenIdentification identification)
	{
		identifications.add(identification);
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getUnitID()
	{
		return unitID;
	}

	public void setUnitID(String unitID)
	{
		this.unitID = unitID;
	}

	public String getUnitGUID()
	{
		return unitGUID;
	}

	public void setUnitGUID(String unitGUID)
	{
		this.unitGUID = unitGUID;
	}

	public String getCollectorsFieldNumber()
	{
		return collectorsFieldNumber;
	}

	public void setCollectorsFieldNumber(String collectorsFieldNumber)
	{
		this.collectorsFieldNumber = collectorsFieldNumber;
	}

	public String getAssemblageID()
	{
		return assemblageID;
	}

	public void setAssemblageID(String assemblageID)
	{
		this.assemblageID = assemblageID;
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

	public String getRecordBasis()
	{
		return recordBasis;
	}

	public void setRecordBasis(String recordBasis)
	{
		this.recordBasis = recordBasis;
	}

	public String getKindOfUnit()
	{
		return kindOfUnit;
	}

	public void setKindOfUnit(String kindOfUnit)
	{
		this.kindOfUnit = kindOfUnit;
	}

	public String getCollectionType()
	{
		return collectionType;
	}

	public void setCollectionType(String collectionType)
	{
		this.collectionType = collectionType;
	}

	public SpecimenTypeStatus getTypeStatus()
	{
		return typeStatus;
	}

	public void setTypeStatus(SpecimenTypeStatus typeStatus)
	{
		this.typeStatus = typeStatus;
	}

	public Sex getSex()
	{
		return sex;
	}

	public void setSex(Sex sex)
	{
		this.sex = sex;
	}

	public PhaseOrStage getPhaseOrStage()
	{
		return phaseOrStage;
	}

	public void setPhaseOrStage(PhaseOrStage phaseOrStage)
	{
		this.phaseOrStage = phaseOrStage;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getPreparationType()
	{
		return preparationType;
	}

	public void setPreparationType(String preparationType)
	{
		this.preparationType = preparationType;
	}

	public int getNumberOfSpecimen()
	{
		return numberOfSpecimen;
	}

	public void setNumberOfSpecimen(int numberOfSpecimen)
	{
		this.numberOfSpecimen = numberOfSpecimen;
	}

	public boolean isFromCaptivity()
	{
		return fromCaptivity;
	}

	public void setFromCaptivity(boolean fromCaptivity)
	{
		this.fromCaptivity = fromCaptivity;
	}

	public boolean isObjectPublic()
	{
		return objectPublic;
	}

	public void setObjectPublic(boolean objectPublic)
	{
		this.objectPublic = objectPublic;
	}

	public boolean isMultiMediaPublic()
	{
		return multiMediaPublic;
	}

	public void setMultiMediaPublic(boolean multiMediaPublic)
	{
		this.multiMediaPublic = multiMediaPublic;
	}

	public Agent getAcquiredFrom()
	{
		return acquiredFrom;
	}

	public void setAcquiredFrom(Agent acquiredFrom)
	{
		this.acquiredFrom = acquiredFrom;
	}

	public GatheringEvent getGatheringEvent()
	{
		return gatheringEvent;
	}

	public void setGatheringEvent(GatheringEvent gatheringEvent)
	{
		this.gatheringEvent = gatheringEvent;
	}

	public List<SpecimenIdentification> getIdentifications()
	{
		return identifications;
	}

	public void setIdentifications(List<SpecimenIdentification> identifications)
	{
		this.identifications = identifications;
	}

	public List<Specimen> getOtherSpecimensInAssemblage()
	{
		return otherSpecimensInAssemblage;
	}

	public void setOtherSpecimensInAssemblage(List<Specimen> otherSpecimensInAssemblage)
	{
		this.otherSpecimensInAssemblage = otherSpecimensInAssemblage;
	}

	public List<Taxon> getAssociatedTaxa()
	{
		return associatedTaxa;
	}

	public void setAssociatedTaxa(List<Taxon> associatedTaxa)
	{
		this.associatedTaxa = associatedTaxa;
	}

}
