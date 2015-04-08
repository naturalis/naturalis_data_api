package nl.naturalis.nda.domain;

public class Person extends Agent {

	private String fullName;
	private Organization organization;


	public Person()
	{
	}


	public Person(String fullName)
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
