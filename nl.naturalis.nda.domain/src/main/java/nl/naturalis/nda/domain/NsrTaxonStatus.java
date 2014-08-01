package nl.naturalis.nda.domain;

public class NsrTaxonStatus {

	private String status;
	private String referenceTitle;
	private String referenceAuthor;
	private String referenceDate;


	public String getStatus()
	{
		return status;
	}


	public void setStatus(String status)
	{
		this.status = status;
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
