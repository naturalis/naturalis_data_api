package nl.naturalis.nda.domain;

public class OrganizationAgent extends Agent {

	private String name;


	public OrganizationAgent()
	{
	}


	public OrganizationAgent(String name)
	{
		this.name = name;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}

}
