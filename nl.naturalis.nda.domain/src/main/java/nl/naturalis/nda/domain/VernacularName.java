package nl.naturalis.nda.domain;

public class VernacularName {

	private String name;
	private String language;
	private boolean preferred;

	private Expert expert;


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


	public boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(boolean preferred)
	{
		this.preferred = preferred;
	}


	public Expert getExpert()
	{
		return expert;
	}


	public void setExpert(Expert expert)
	{
		this.expert = expert;
	}

}
