package nl.naturalis.nda.domain.systypes;

public class CoLCommonName {

	private int taxonId;
	private String vernacularName;
	private String language;
	private String countryCode;
	private String locality;
	private String transliteration;

	private CoLTaxon taxon;


	public int getTaxonId()
	{
		return taxonId;
	}


	public void setTaxonId(int taxonId)
	{
		this.taxonId = taxonId;
	}


	public String getVernacularName()
	{
		return vernacularName;
	}


	public void setVernacularName(String vernacularName)
	{
		this.vernacularName = vernacularName;
	}


	public String getLanguage()
	{
		return language;
	}


	public void setLanguage(String language)
	{
		this.language = language;
	}


	public String getCountryCode()
	{
		return countryCode;
	}


	public void setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
	}


	public String getLocality()
	{
		return locality;
	}


	public void setLocality(String locality)
	{
		this.locality = locality;
	}


	public String getTransliteration()
	{
		return transliteration;
	}


	public void setTransliteration(String transliteration)
	{
		this.transliteration = transliteration;
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
