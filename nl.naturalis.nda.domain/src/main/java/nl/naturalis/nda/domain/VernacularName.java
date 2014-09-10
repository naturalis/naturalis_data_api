package nl.naturalis.nda.domain;

public class VernacularName {

	private String name;
	private String language;
	private Boolean preferred;

	private Expert nameAccordingTo;


	public VernacularName()
	{
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


	public Boolean isPreferred()
	{
		return preferred;
	}


	public void setPreferred(Boolean preferred)
	{
		this.preferred = preferred;
	}


	public Expert getNameAccordingTo()
	{
		return nameAccordingTo;
	}


	public void setNameAccordingTo(Expert expert)
	{
		this.nameAccordingTo = expert;
	}

}
