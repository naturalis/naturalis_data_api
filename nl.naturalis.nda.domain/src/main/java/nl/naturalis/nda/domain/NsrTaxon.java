package nl.naturalis.nda.domain;

import java.util.List;

public class NsrTaxon {

	private int id;
	private int parentId;
	private String nsrId;
	private String url;
	private String rank;

	private NsrTaxonStatus status;
	private NsrScientificName acceptedName;

	private List<NsrSynonym> synonyms;
	private List<NsrCommonName> commonNames;
	private List<NsrTaxonDescription> descriptions;


	public int getId()
	{
		return id;
	}


	public void setId(int id)
	{
		this.id = id;
	}


	public int getParentId()
	{
		return parentId;
	}


	public void setParentId(int parentId)
	{
		this.parentId = parentId;
	}


	public String getNsrId()
	{
		return nsrId;
	}


	public void setNsrId(String nsrId)
	{
		this.nsrId = nsrId;
	}


	public String getUrl()
	{
		return url;
	}


	public void setUrl(String url)
	{
		this.url = url;
	}


	public String getRank()
	{
		return rank;
	}


	public void setRank(String rank)
	{
		this.rank = rank;
	}


	public NsrTaxonStatus getStatus()
	{
		return status;
	}


	public void setStatus(NsrTaxonStatus status)
	{
		this.status = status;
	}


	public NsrScientificName getAcceptedName()
	{
		return acceptedName;
	}


	public void setAcceptedName(NsrScientificName acceptedName)
	{
		this.acceptedName = acceptedName;
	}


	public List<NsrSynonym> getSynonyms()
	{
		return synonyms;
	}


	public void setSynonyms(List<NsrSynonym> synonyms)
	{
		this.synonyms = synonyms;
	}


	public List<NsrCommonName> getCommonNames()
	{
		return commonNames;
	}


	public void setCommonNames(List<NsrCommonName> commonNames)
	{
		this.commonNames = commonNames;
	}


	public List<NsrTaxonDescription> getDescriptions()
	{
		return descriptions;
	}


	public void setDescriptions(List<NsrTaxonDescription> descriptions)
	{
		this.descriptions = descriptions;
	}

}
