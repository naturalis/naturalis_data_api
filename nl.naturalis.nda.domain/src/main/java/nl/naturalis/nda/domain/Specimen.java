package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Specimen {

	private String sourceSystemName;
	private String sourceSystemId;
	private Date sourceSystemDateCreated;
	private String specimenId;
	private String recordBasis;
	private String kindOfUnit;
	private String sourceInstitutionID;
	private String phylum;
	private String sex;
	private String phaseOrStage;
	private String accessionSpecimenNumbers;
	private String altitude;
	private String depth;
	private String gatheringAgent;
	private String worldRegion;
	private String country;
	private String provinceState;
	private String locality;
	private boolean publicObject;
	private String altitudeUnit;
	private String depthUnit;
	private String collectingStartDate;
	private String collectingEndDate;
	private String title;
	private String taxonCoverage;
	private String multiMediaPublic;
	private String latitudeDecimal;
	private String longitudeDecimal;
	private String geodeticDatum;

	private List<Determination> determinations;


	public void addDetermination(Determination determination)
	{
		if (determinations == null) {
			determinations = new ArrayList<Determination>(4);
		}
		determinations.add(determination);
	}


	public String getSourceSystemName()
	{
		return sourceSystemName;
	}


	public void setSourceSystemName(String sourceSystemName)
	{
		this.sourceSystemName = sourceSystemName;
	}


	public String getSourceSystemId()
	{
		return sourceSystemId;
	}


	public void setSourceSystemId(String sourceSystemId)
	{
		this.sourceSystemId = sourceSystemId;
	}


	public Date getSourceSystemDateCreated()
	{
		return sourceSystemDateCreated;
	}


	public void setSourceSystemDateCreated(Date sourceSystemDateCreated)
	{
		this.sourceSystemDateCreated = sourceSystemDateCreated;
	}


	public String getSpecimenId()
	{
		return specimenId;
	}


	public void setSpecimenId(String specimenId)
	{
		this.specimenId = specimenId;
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


	public String getSourceInstitutionID()
	{
		return sourceInstitutionID;
	}


	public void setSourceInstitutionID(String sourceInstitutionID)
	{
		this.sourceInstitutionID = sourceInstitutionID;
	}


	public String getPhylum()
	{
		return phylum;
	}


	public void setPhylum(String phylum)
	{
		this.phylum = phylum;
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


	public String getAltitude()
	{
		return altitude;
	}


	public void setAltitude(String altitude)
	{
		this.altitude = altitude;
	}


	public String getDepth()
	{
		return depth;
	}


	public void setDepth(String depth)
	{
		this.depth = depth;
	}


	public String getGatheringAgent()
	{
		return gatheringAgent;
	}


	public void setGatheringAgent(String gatheringAgent)
	{
		this.gatheringAgent = gatheringAgent;
	}


	public String getWorldRegion()
	{
		return worldRegion;
	}


	public void setWorldRegion(String worldRegion)
	{
		this.worldRegion = worldRegion;
	}


	public String getCountry()
	{
		return country;
	}


	public void setCountry(String country)
	{
		this.country = country;
	}


	public String getProvinceState()
	{
		return provinceState;
	}


	public void setProvinceState(String provinceState)
	{
		this.provinceState = provinceState;
	}


	public String getLocality()
	{
		return locality;
	}


	public void setLocality(String locality)
	{
		this.locality = locality;
	}


	public boolean isPublicObject()
	{
		return publicObject;
	}


	public void setPublicObject(boolean publicObject)
	{
		this.publicObject = publicObject;
	}


	public String getAltitudeUnit()
	{
		return altitudeUnit;
	}


	public void setAltitudeUnit(String altitudeUnit)
	{
		this.altitudeUnit = altitudeUnit;
	}


	public String getDepthUnit()
	{
		return depthUnit;
	}


	public void setDepthUnit(String depthUnit)
	{
		this.depthUnit = depthUnit;
	}


	public String getCollectingStartDate()
	{
		return collectingStartDate;
	}


	public void setCollectingStartDate(String collectingStartDate)
	{
		this.collectingStartDate = collectingStartDate;
	}


	public String getCollectingEndDate()
	{
		return collectingEndDate;
	}


	public void setCollectingEndDate(String collectingEndDate)
	{
		this.collectingEndDate = collectingEndDate;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getTaxonCoverage()
	{
		return taxonCoverage;
	}


	public void setTaxonCoverage(String taxonCoverage)
	{
		this.taxonCoverage = taxonCoverage;
	}


	public String getMultiMediaPublic()
	{
		return multiMediaPublic;
	}


	public void setMultiMediaPublic(String multiMediaPublic)
	{
		this.multiMediaPublic = multiMediaPublic;
	}


	public String getLatitudeDecimal()
	{
		return latitudeDecimal;
	}


	public void setLatitudeDecimal(String latitudeDecimal)
	{
		this.latitudeDecimal = latitudeDecimal;
	}


	public String getLongitudeDecimal()
	{
		return longitudeDecimal;
	}


	public void setLongitudeDecimal(String longitudeDecimal)
	{
		this.longitudeDecimal = longitudeDecimal;
	}


	public String getGeodeticDatum()
	{
		return geodeticDatum;
	}


	public void setGeodeticDatum(String geodeticDatum)
	{
		this.geodeticDatum = geodeticDatum;
	}


	public List<Determination> getDeterminations()
	{
		return determinations;
	}


	public void setDeterminations(List<Determination> determinations)
	{
		this.determinations = determinations;
	}

}
