package nl.naturalis.nda.domain.systypes;

public class NsrCommonName extends NsrName {

	private String name;
	private String expert;
	private String organisation;
	private String referenceTitle;
	private String referenceAuthor;
	private String referenceDate;
	private boolean preferred;
	private String language;

	private NsrTaxon taxon;


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


	public boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}


	public String getLanguage()
	{
		return language;
	}


	public void setLanguage(String language)
	{
		this.language = language;
	}


	public NsrTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(NsrTaxon taxon)
	{
		this.taxon = taxon;
	}

}
