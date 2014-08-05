package nl.naturalis.nda.domain.systypes;

public class CoLReference {

	private int taxonID;
	private String creator;
	private String date;
	private String title;
	private String description;
	private String identifier;
	private String type;

	private CoLTaxon taxon;


	public int getTaxonID()
	{
		return taxonID;
	}


	public void setTaxonID(int taxonID)
	{
		this.taxonID = taxonID;
	}


	public String getCreator()
	{
		return creator;
	}


	public void setCreator(String creator)
	{
		this.creator = creator;
	}


	public String getDate()
	{
		return date;
	}


	public void setDate(String date)
	{
		this.date = date;
	}


	public String getTitle()
	{
		return title;
	}


	public void setTitle(String title)
	{
		this.title = title;
	}


	public String getDescription()
	{
		return description;
	}


	public void setDescription(String description)
	{
		this.description = description;
	}


	public String getIdentifier()
	{
		return identifier;
	}


	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}


	public String getType()
	{
		return type;
	}


	public void setType(String type)
	{
		this.type = type;
	}


	public CoLTaxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(CoLTaxon taxon)
	{
		this.taxon = taxon;
	}
}
