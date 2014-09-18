package nl.naturalis.nda.domain;

public class Organization extends Agent {

	private String name;


	public Organization()
	{
	}


	public Organization(String name)
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
