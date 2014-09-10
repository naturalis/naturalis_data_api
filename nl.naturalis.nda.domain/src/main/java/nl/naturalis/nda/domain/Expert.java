package nl.naturalis.nda.domain;

/**
 * An {@code Expert} represents the person and/or organization on whose
 * authority a scientific name or common name was included in the source system
 * (e.g. the Catalogue of Life). Sometimes the expert is an anonymous person
 * within an organization, in which case only the name of the organization is
 * set; sometimes the agent does not work on behalf of an organization, or the
 * organization is not known.
 * 
 */
public class Expert {

	private String fullName;
	private String organization;


	public String getFullName()
	{
		return fullName;
	}


	public void setFullName(String fullName)
	{
		this.fullName = fullName;
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
