package nl.naturalis.nda.domain.systypes;

public class NsrName {

	private String name;
	private String expert;
	private String organisation;
	private String referenceTitle;
	private String referenceAuthor;
	private String referenceDate;


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getExpert()
	{
		return expert;
	}


	public void setExpert(String expert)
	{
		this.expert = expert;
	}


	public String getOrganisation()
	{
		return organisation;
	}


	public void setOrganisation(String organisation)
	{
		this.organisation = organisation;
	}


	public String getReferenceTitle()
	{
		return referenceTitle;
	}


	public void setReferenceTitle(String referenceTitle)
	{
		this.referenceTitle = referenceTitle;
	}


	public String getReferenceAuthor()
	{
		return referenceAuthor;
	}


	public void setReferenceAuthor(String referenceAuthor)
	{
		this.referenceAuthor = referenceAuthor;
	}


	public String getReferenceDate()
	{
		return referenceDate;
	}


	public void setReferenceDate(String referenceDate)
	{
		this.referenceDate = referenceDate;
	}

}
