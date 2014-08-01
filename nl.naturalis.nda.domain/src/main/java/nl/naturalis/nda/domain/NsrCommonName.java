package nl.naturalis.nda.domain;

public class NsrCommonName extends NsrName {

	private NsrTaxon taxon;
	private boolean preferred;
	private String language;


	public NsrTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(NsrTaxon taxon)
	{
		this.taxon = taxon;
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

}
