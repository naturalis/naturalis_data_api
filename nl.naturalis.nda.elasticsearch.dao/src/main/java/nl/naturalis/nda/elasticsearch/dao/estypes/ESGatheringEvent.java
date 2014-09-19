package nl.naturalis.nda.elasticsearch.dao.estypes;

import java.util.Date;
import java.util.List;

import nl.naturalis.nda.domain.GatheringSiteCoordinates;
import nl.naturalis.nda.domain.Organization;
import nl.naturalis.nda.domain.Person;

public class ESGatheringEvent {

	private String worldRegion;
	private String continent;
	private String country;
	private String iso3166Code;
	private String provinceState;
	private String island;
	private String locality;
	private String city;
	private String sublocality;
	private String localityText;
	private Date dateTimeBegin;
	private Date dateTimeEnd;
	private String method;
	private String altitude;
	private String altitudeUnifOfMeasurement;
	private String dept;
	private String deptUnitOfMeasurement;
	private List<Person> gatheringPersons;
	private List<Organization> gatheringOrganizations;
	private List<GatheringSiteCoordinates> siteCoordinates;


	public String getWorldRegion()
	{
		return worldRegion;
	}


	public void setWorldRegion(String worldRegion)
	{
		this.worldRegion = worldRegion;
	}


	public String getContinent()
	{
		return continent;
	}


	public void setContinent(String continent)
	{
		this.continent = continent;
	}


	public String getCountry()
	{
		return country;
	}


	public void setCountry(String country)
	{
		this.country = country;
	}


	public String getIso3166Code()
	{
		return iso3166Code;
	}


	public void setIso3166Code(String iso3166Code)
	{
		this.iso3166Code = iso3166Code;
	}


	public String getProvinceState()
	{
		return provinceState;
	}


	public void setProvinceState(String provinceState)
	{
		this.provinceState = provinceState;
	}


	public String getIsland()
	{
		return island;
	}


	public void setIsland(String island)
	{
		this.island = island;
	}


	public String getLocality()
	{
		return locality;
	}


	public void setLocality(String locality)
	{
		this.locality = locality;
	}


	public String getCity()
	{
		return city;
	}


	public void setCity(String city)
	{
		this.city = city;
	}


	public String getSublocality()
	{
		return sublocality;
	}


	public void setSublocality(String sublocality)
	{
		this.sublocality = sublocality;
	}


	public String getLocalityText()
	{
		return localityText;
	}


	public void setLocalityText(String localityText)
	{
		this.localityText = localityText;
	}


	public Date getDateTimeBegin()
	{
		return dateTimeBegin;
	}


	public void setDateTimeBegin(Date dateTimeBegin)
	{
		this.dateTimeBegin = dateTimeBegin;
	}


	public Date getDateTimeEnd()
	{
		return dateTimeEnd;
	}


	public void setDateTimeEnd(Date dateTimeEnd)
	{
		this.dateTimeEnd = dateTimeEnd;
	}


	public String getMethod()
	{
		return method;
	}


	public void setMethod(String method)
	{
		this.method = method;
	}


	public String getAltitude()
	{
		return altitude;
	}


	public void setAltitude(String altitude)
	{
		this.altitude = altitude;
	}


	public String getAltitudeUnifOfMeasurement()
	{
		return altitudeUnifOfMeasurement;
	}


	public void setAltitudeUnifOfMeasurement(String altitudeUnifOfMeasurement)
	{
		this.altitudeUnifOfMeasurement = altitudeUnifOfMeasurement;
	}


	public String getDept()
	{
		return dept;
	}


	public void setDept(String dept)
	{
		this.dept = dept;
	}


	public String getDeptUnitOfMeasurement()
	{
		return deptUnitOfMeasurement;
	}


	public void setDeptUnitOfMeasurement(String deptUnitOfMeasurement)
	{
		this.deptUnitOfMeasurement = deptUnitOfMeasurement;
	}


	public List<Person> getGatheringPersons()
	{
		return gatheringPersons;
	}


	public void setGatheringPersons(List<Person> persons)
	{
		this.gatheringPersons = persons;
	}


	public List<Organization> getGatheringOrganizations()
	{
		return gatheringOrganizations;
	}


	public void setGatheringOrganizations(List<Organization> organizations)
	{
		this.gatheringOrganizations = organizations;
	}


	public List<GatheringSiteCoordinates> getSiteCoordinates()
	{
		return siteCoordinates;
	}


	public void setSiteCoordinates(List<GatheringSiteCoordinates> siteCoordinates)
	{
		this.siteCoordinates = siteCoordinates;
	}

}
