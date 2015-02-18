package nl.naturalis.nda.export.dwca;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "archive")
@XmlType(propOrder = {"unitID", "unitGUID", "collectorsFieldNumber", "assemblageID",
		              "sourceInstitutionID", "sourceID", "owner", "licenceType", "licence",
		              "recordBasis", "kindOfUnit", "collectionType", "typeStatus","sex",
		              "phaseOrStage","title","notes","preparationType","numberOfSpecimen","fromCaptivity","objectPublic","multiMediaPublic"})

public class Meta
{
	private String unitID;
	private String unitGUID;
	private String collectorsFieldNumber;
	private String assemblageID;
	private String sourceInstitutionID;
	private String sourceID;
	private String owner;
	private String licenceType;
	private String licence;
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

}
