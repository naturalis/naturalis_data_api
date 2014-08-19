package nl.naturalis.nda.domain;

public class SpecimenIdentification extends TaxonomicIdentification {

	private boolean preferred;
	private String verificationStatus;
	private String typeStatus;
	private String remarks;
	private String references;


	public boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}


	public String getVerificationStatus()
	{
		return verificationStatus;
	}


	public void setVerificationStatus(String verificationStatus)
	{
		this.verificationStatus = verificationStatus;
	}


	public String getTypeStatus()
	{
		return typeStatus;
	}


	public void setTypeStatus(String typeStatus)
	{
		this.typeStatus = typeStatus;
	}


	public String getRemarks()
	{
		return remarks;
	}


	public void setRemarks(String remarks)
	{
		this.remarks = remarks;
	}


	public String getReferences()
	{
		return references;
	}


	public void setReferences(String references)
	{
		this.references = references;
	}

}
