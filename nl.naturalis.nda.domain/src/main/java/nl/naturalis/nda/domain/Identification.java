package nl.naturalis.nda.domain;

import java.util.Date;

public class Identification {

	private boolean preferred;
	private String identifiedBy;
	private Date dateIdentified;
	private String typeStatus;
	private String qualifier1;
	private String qualifier2;
	private String qualifier3;
	private String remarks;
	private String references;

	private Taxon taxon;
	private Occurrence occurrence;


	public boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}


	public String getIdentifiedBy()
	{
		return identifiedBy;
	}


	public void setIdentifiedBy(String identifiedBy)
	{
		this.identifiedBy = identifiedBy;
	}


	public Date getDateIdentified()
	{
		return dateIdentified;
	}


	public void setDateIdentified(Date dateIdentified)
	{
		this.dateIdentified = dateIdentified;
	}


	public String getTypeStatus()
	{
		return typeStatus;
	}


	public void setTypeStatus(String typeStatus)
	{
		this.typeStatus = typeStatus;
	}


	public String getQualifier1()
	{
		return qualifier1;
	}


	public void setQualifier1(String qualifier1)
	{
		this.qualifier1 = qualifier1;
	}


	public String getQualifier2()
	{
		return qualifier2;
	}


	public void setQualifier2(String qualifier2)
	{
		this.qualifier2 = qualifier2;
	}


	public String getQualifier3()
	{
		return qualifier3;
	}


	public void setQualifier3(String qualifier3)
	{
		this.qualifier3 = qualifier3;
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


	public Taxon getTaxon()
	{
		return taxon;
	}


	public void setTaxon(Taxon taxon)
	{
		this.taxon = taxon;
	}


	public Occurrence getOccurrence()
	{
		return occurrence;
	}


	public void setOccurrence(Occurrence occurrence)
	{
		this.occurrence = occurrence;
	}

}
