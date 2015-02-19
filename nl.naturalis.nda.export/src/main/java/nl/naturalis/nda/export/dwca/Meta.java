package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
public class Meta
{
	@XmlAttribute(name = "metadata")
	private String metadata;
	@XmlAttribute(name = "xmlns:xsi")
	private String xmlnsxsi;
	@XmlAttribute(name = "xmlns:tdwg")
	private String xmlnstdwg;
	@XmlElement(name = "core")
	List<Core> cores;

	@XmlElement(name = "unitid")
	private String unitID;
	@XmlElement(name = "unitguid")
	private String unitGUID;
	@XmlElement(name = "collectorsfieldnumber")
	private String collectorsFieldNumber;
	@XmlElement(name = "assemblageid")
	private String assemblageID;
	@XmlElement(name = "sourceinstitutionid")
	private String sourceInstitutionID;
	@XmlElement(name = "sourceid")
	private String sourceID;
	@XmlElement(name = "owner")
	private String owner;
	@XmlElement(name = "licencetype")
	private String licenceType;
	@XmlElement(name = "licence")
	private String licence;
	@XmlElement(name = "recordbasis")
	private String recordBasis;
	@XmlElement(name = "kindofunit")
	private String kindOfUnit;
	@XmlElement(name = "collectiontype")
	private String collectionType;
	@XmlElement(name = "typestatus")
	private String typeStatus;
	@XmlElement(name = "sex")
	private String sex;
	@XmlElement(name = "phaseorstage")
	private String phaseOrStage;
	@XmlElement(name = "title")
	private String title;
	@XmlElement(name = "notes")
	private String notes;
	@XmlElement(name = "preparationtype")
	private String preparationType;
	@XmlElement(name = "numberofspecimen")
	private int numberOfSpecimen;
	@XmlElement(name = "fromcaptivity")
	private boolean fromCaptivity;
	@XmlElement(name = "objectpublic")
	private boolean objectPublic;
	@XmlElement(name = "multimediapublic")
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

	public String getMetadata()
	{
		return metadata;
	}

	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	public String getXmlnsxsi()
	{
		return xmlnsxsi;
	}

	public void setXmlnsxsi(String xmlnsxsi)
	{
		this.xmlnsxsi = xmlnsxsi;
	}

	public String getXmlnstdwg()
	{
		return xmlnstdwg;
	}

	public void setXmlnstdwg(String xmlnstdwg)
	{
		this.xmlnstdwg = xmlnstdwg;
	}

	public List<Core> getCores()
	{
		return cores;
	}

	public void setCores(List<Core> cores)
	{
		this.cores = cores;
	}

	public void add(Core cores)
	{
		if (this.cores == null)
		{
			this.cores = new ArrayList<Core>();
		}
		this.cores.add(cores);

	}

}
