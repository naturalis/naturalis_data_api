package nl.naturalis.nda.domain;

/**
 * An {@code Expert} represents the person and/or organization on whose
 * authority a scientific name or common name was included in the source system
 * (e.g. the Catalogue of Life). Sometimes the expert is an anonymous person
 * within an organization, in which case only the name of the organization is
 * set.
 * 
 */
public class Expert {

	private String name;
	private String organization;


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getOrganization()
	{
		return organization;
	}


	public void setOrganization(String organization)
	{
		this.organization = organization;
	}

}
