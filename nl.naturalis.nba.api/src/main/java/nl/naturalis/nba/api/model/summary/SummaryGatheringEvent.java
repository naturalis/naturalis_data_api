package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import java.util.Date;
import java.util.List;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Organization;

public class SummaryGatheringEvent implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
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
