package nl.naturalis.nba.api.model;

import java.util.Date;
import java.util.List;

public class SummaryGatheringEvent implements INbaModelObject {

	private String localityText;
	private Date dateTimeBegin;
	private Date dateTimeEnd;
	private List<SummaryPerson> gatheringPersons;
	private List<Organization> gatheringOrganizations;
	private List<SummaryGatheringSiteCoordinates> siteCoordinates;
	
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
	
	public List<SummaryPerson> getGatheringPersons()
	{
		return gatheringPersons;
	}
	
	public void setGatheringPersons(List<SummaryPerson> gatheringPersons)
	{
		this.gatheringPersons = gatheringPersons;
	}
	
	public List<Organization> getGatheringOrganizations()
	{
		return gatheringOrganizations;
	}
	
	public void setGatheringOrganizations(List<Organization> gatheringOrganizations)
	{
		this.gatheringOrganizations = gatheringOrganizations;
	}
	
	public List<SummaryGatheringSiteCoordinates> getSiteCoordinates()
	{
		return siteCoordinates;
	}
	
	public void setSiteCoordinates(List<SummaryGatheringSiteCoordinates> siteCoordinates)
	{
		this.siteCoordinates = siteCoordinates;
	}

}
