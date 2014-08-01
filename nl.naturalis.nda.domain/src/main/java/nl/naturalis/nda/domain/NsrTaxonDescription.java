package nl.naturalis.nda.domain;

public class NsrTaxonDescription {

	private NsrTaxon taxon;
	private String category;
	private String description;


	public NsrTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(NsrTaxon taxon)
	{
		this.taxon = taxon;
	}


	public String getCategory()
	{
		return category;
	}


	public void setCategory(String category)
	{
		this.category = category;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}

}
