package nl.naturalis.nda.search;

import nl.naturalis.nda.domain.Taxon;

public class NameResolutionInfo {

	private String path;
	private String value;
	private String acceptedName;
	private Taxon taxon;


	/**
	 * The field within the {@link Taxon} object that matched the search term.
	 * @return
	 */
	public String getPath()
	{
		return path;
	}


	public void setPath(String path)
	{
		this.path = path;
	}


	public String getValue()
	{
		return value;
	}


	public void setValue(String value)
	{
		this.value = value;
	}


	public String getAcceptedName()
	{
		return acceptedName;
	}


	public void setAcceptedName(String acceptedName)
	{
		this.acceptedName = acceptedName;
	}


	public Taxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(Taxon taxon)
	{
		this.taxon = taxon;
	}

}
