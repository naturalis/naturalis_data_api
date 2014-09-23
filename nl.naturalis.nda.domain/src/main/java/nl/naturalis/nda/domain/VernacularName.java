package nl.naturalis.nda.domain;

import java.util.List;

public class VernacularName {

	private String name;
	private String language;
	private Boolean preferred;

	private List<Reference> references;
	private List<Expert> experts;


	public VernacularName()
	{
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof VernacularName)) {
			return false;
		}
		VernacularName other = (VernacularName) obj;
		return name.equals(other.name) && language.equals(other.language);
	}


	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + name.hashCode();
		hash = (hash * 31) + language.hashCode();
		return hash;
	}


	public VernacularName(String name)
	{
		this.name = name;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getLanguage()
	{
		return language;
	}


	public void setLanguage(String language)
	{
		this.language = language;
	}


	public Boolean getPreferred()
	{
		return preferred;
	}


	public void setPreferred(Boolean preferred)
	{
		this.preferred = preferred;
	}


	public List<Reference> getReferences()
	{
		return references;
	}


	public void setReferences(List<Reference> references)
	{
		this.references = references;
	}


	public List<Expert> getExperts()
	{
		return experts;
	}


	public void setExperts(List<Expert> experts)
	{
		this.experts = experts;
	}

}
