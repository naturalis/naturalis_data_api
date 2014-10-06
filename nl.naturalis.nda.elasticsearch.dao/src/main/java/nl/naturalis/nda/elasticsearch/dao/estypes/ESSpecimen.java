package nl.naturalis.nda.elasticsearch.dao.estypes;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.NdaTraceableObject;
import nl.naturalis.nda.domain.SpecimenIdentification;

public class ESSpecimen extends NdaTraceableObject {

	private String unitID;
	private String unitGUID;
	private String assemblageID;
	private String sourceInstitutionID;
	private String recordBasis;
	private String kindOfUnit;
	private String collectionType;
	private String typeStatus;
	private String sex;
	private String phaseOrStage;
	private String title;
	private String notes;
	private String preparationType;
	private int numberOfSpecimen;
	private boolean fromCaptivity;
	private boolean objectPublic;
	private boolean multiMediaPublic;

	private Agent acquiredFrom;
	private ESGatheringEvent gatheringEvent;
	private List<SpecimenIdentification> identifications;


	public void addIndentification(SpecimenIdentification identification)
	{
		if (identifications == null) {
			identifications = new ArrayList<SpecimenIdentification>();
		}
		identifications.add(identification);
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


	public String getTypeStatus()
	{
		return typeStatus;
	}


	public void setTypeStatus(String typeStatus)
	{
		this.typeStatus = typeStatus;
	}


	public String getSex()
	{
		return sex;
	}


	public void setSex(String sex)
	{
		this.sex = sex;
	}


	public String getPhaseOrStage()
	{
		return phaseOrStage;
	}


	public void setPhaseOrStage(String phaseOrStage)
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


	public ESGatheringEvent getGatheringEvent()
	{
		return gatheringEvent;
	}


	public void setGatheringEvent(ESGatheringEvent gatheringEvent)
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

}
