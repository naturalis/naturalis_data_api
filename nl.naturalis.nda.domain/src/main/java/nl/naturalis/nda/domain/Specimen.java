package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.List;

public class Specimen extends NdaTraceableObject {

	private String unitID;
	private String unitGUID;
	private String setID;
	private String sourceInstitutionID;
	private String recordBasis;
	private String kindOfUnit;
	private String collectionType;
	private String sex;
	private String phaseOrStage;
	private String accessionSpecimenNumbers;
	private String title;
	private boolean objectPublic;
	private boolean multiMediaPublic;

	private GatheringEvent gatheringEvent;
	private List<GatheringSiteCoordinates> siteCoordinates;
	private List<SpecimenIdentification> identifications;
	private List<Specimen> otherSpecimensInSet;
	private List<Taxon> associatedTaxa;


	public void addIndentification(SpecimenIdentification identification)
	{
		if (identifications == null) {
			identifications = new ArrayList<SpecimenIdentification>();
		}
		identifications.add(identification);
	}


	public void addOtherSpecimenToSet(Specimen specimen)
	{
		if (otherSpecimensInSet == null) {
			otherSpecimensInSet = new ArrayList<Specimen>();
		}
		otherSpecimensInSet.add(specimen);
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


	public String getSetID()
	{
		return setID;
	}


	public void setSetID(String setID)
	{
		this.setID = setID;
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


	public String getAccessionSpecimenNumbers()
	{
		return accessionSpecimenNumbers;
	}


	public void setAccessionSpecimenNumbers(String accessionSpecimenNumbers)
	{
		this.accessionSpecimenNumbers = accessionSpecimenNumbers;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
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


	public GatheringEvent getGatheringEvent()
	{
		return gatheringEvent;
	}


	public void setGatheringEvent(GatheringEvent gatheringEvent)
	{
		this.gatheringEvent = gatheringEvent;
	}


	public List<GatheringSiteCoordinates> getSiteCoordinates()
	{
		return siteCoordinates;
	}


	public void setSiteCoordinates(List<GatheringSiteCoordinates> siteCoordinates)
	{
		this.siteCoordinates = siteCoordinates;
	}


	public List<SpecimenIdentification> getIdentifications()
	{
		return identifications;
	}


	public void setIdentifications(List<SpecimenIdentification> identifications)
	{
		this.identifications = identifications;
	}


	public List<Specimen> getOtherSpecimensInSet()
	{
		return otherSpecimensInSet;
	}


	public void setOtherSpecimensInSet(List<Specimen> otherSpecimensInSet)
	{
		this.otherSpecimensInSet = otherSpecimensInSet;
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
