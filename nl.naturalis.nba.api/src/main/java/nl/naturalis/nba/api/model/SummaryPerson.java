package nl.naturalis.nba.api.model;

public class SummaryPerson implements INbaModelObject {

	private String fullName;
	private Organization organization;

	public SummaryPerson()
	{
	}

	public SummaryPerson(String fullName)
	{
		this.fullName = fullName;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}
}
