package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Occurrence {

	private String unitID;
	private String recordBasis;
	private String kindOfUnit;
	private String sourceInstitutionID;
	private String collectionType;
	private String sex;
	private String unitGUID;
	private String phaseOrStage;
	private String accessionSpecimenNumbers;
	private int altitude;
	private int depth;
	private String gatheringAgent;
	private String worldRegion;
	private String country;
	private String provinceState;
	private String locality;
	private boolean objectPublic;
	private String altitudeUnit;
	private String depthUnit;
	private Date collectingStartDate;
	private Date collectingEndDate;
	private String title;
	private String taxonCoverage;
	private String multiMediaPublic;
	private String latitudeDecimal;
	private String longitudeDecimal;
	private String geodeticDatum;
	private String url;

	List<Identification> identifications;


	public void addIndentification(Identification identification)
	{
		if (identifications == null) {
			identifications = new ArrayList<Identification>();
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


	public String getUnitGUID()
	{
		return unitGUID;
	}


	public void setUnitGUID(String unitGUID)
	{
		this.unitGUID = unitGUID;
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


	public int getAltitude()
	{
		return altitude;
	}


	public void setAltitude(int altitude)
	{
		this.altitude = altitude;
	}


	public int getDepth()
	{
		return depth;
	}


	public void setDepth(int depth)
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


	public boolean isObjectPublic()
	{
		return objectPublic;
	}


	public void setObjectPublic(boolean objectPublic)
	{
		this.objectPublic = objectPublic;
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


	public Date getCollectingStartDate()
	{
		return collectingStartDate;
	}


	public void setCollectingStartDate(Date collectingStartDate)
	{
		this.collectingStartDate = collectingStartDate;
	}


	public Date getCollectingEndDate()
	{
		return collectingEndDate;
	}


	public void setCollectingEndDate(Date collectingEndDate)
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


	public String getUrl()
	{
		return url;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}


	public List<Identification> getIdentifications()
	{
		return identifications;
	}


	public void setIdentifications(List<Identification> identifications)
	{
		this.identifications = identifications;
	}

}
