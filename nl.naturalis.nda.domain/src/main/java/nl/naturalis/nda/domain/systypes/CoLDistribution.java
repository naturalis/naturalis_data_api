package nl.naturalis.nda.domain.systypes;

public class CoLDistribution {

	private int taxonID;
	private String locationID;
	private String locality;
	private String occurrenceStatus;
	private String establishmentMeans;

	private CoLTaxon taxon;


	public int getTaxonID()
	{
		return taxonID;
	}


	public void setTaxonID(int taxonID)
	{
		this.taxonID = taxonID;
	}


	public String getLocationID()
	{
		return locationID;
	}


	public void setLocationID(String locationID)
	{
		this.locationID = locationID;
	}


	public String getLocality()
	{
		return locality;
	}


	public void setLocality(String locality)
	{
		this.locality = locality;
	}


	public String getOccurrenceStatus()
	{
		return occurrenceStatus;
	}


	public void setOccurrenceStatus(String occurrenceStatus)
	{
		this.occurrenceStatus = occurrenceStatus;
	}


	public String getEstablishmentMeans()
	{
		return establishmentMeans;
	}


	public void setEstablishmentMeans(String establishmentMeans)
	{
		this.establishmentMeans = establishmentMeans;
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
