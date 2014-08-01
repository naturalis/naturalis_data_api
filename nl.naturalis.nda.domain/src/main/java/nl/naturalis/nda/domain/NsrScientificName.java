package nl.naturalis.nda.domain;

public class NsrScientificName extends NsrName {

	private String uninomial;
	private String specificEpithet;
	private String infraSpecificEpithet;
	private String authorship;
	private String authorName;
	private String authorYear;


	public String getUninomial()
	{
		return uninomial;
	}


	public void setUninomial(String uninomial)
	{
		this.uninomial = uninomial;
	}


	public String getSpecificEpithet()
	{
		return specificEpithet;
	}


	public void setSpecificEpithet(String specificEpithet)
	{
		this.specificEpithet = specificEpithet;
	}


	public String getInfraSpecificEpithet()
	{
		return infraSpecificEpithet;
	}


	public void setInfraSpecificEpithet(String infraSpecificEpithet)
	{
		this.infraSpecificEpithet = infraSpecificEpithet;
	}


	public String getAuthorship()
	{
		return authorship;
	}


	public void setAuthorship(String authorship)
	{
		this.authorship = authorship;
	}


	public String getAuthorName()
	{
		return authorName;
	}


	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}


	public String getAuthorYear()
	{
		return authorYear;
	}


	public void setAuthorYear(String authorYear)
	{
		this.authorYear = authorYear;
	}

}
