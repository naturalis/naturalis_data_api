package nl.naturalis.nda.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GatheringEvent {

	private String worldRegion;
	private String country;
	private String provinceState;
	private String island;
	private String locality;
	private String localityText;
	private Date dateTimeBegin;
	private Date dateTimeEnd;
	private String method;
	private String altitude;
	private String altitudeUnifOfMeasurement;
	private String dept;
	private String deptUnitOfMeasurement;
	private List<GatheringAgent> gatheringAgents;
	private List<GatheringSiteCoordinates> siteCoordinates;


	public void addGatheringAgent(GatheringAgent agent)
	{
		if (gatheringAgents == null) {
			gatheringAgents = new ArrayList<GatheringAgent>();
		}
		gatheringAgents.add(agent);
	}


	public void addSiteCoordinates(GatheringSiteCoordinates coordinates)
	{
		if (siteCoordinates == null) {
			siteCoordinates = new ArrayList<GatheringSiteCoordinates>();
		}
		siteCoordinates.add(coordinates);
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


	public List<GatheringAgent> getGatheringAgents()
	{
		return gatheringAgents;
	}


	public void setGatheringAgents(List<GatheringAgent> gatheringAgents)
	{
		this.gatheringAgents = gatheringAgents;
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
