package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.VernacularName;

/**
 * A miniature version of {@link VernacularName}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryVernacularName implements INbaModelObject {

	@Analyzers({ DEFAULT, CASE_INSENSITIVE, LIKE })
	private String name;
	private String language;

	public SummaryVernacularName()
	{
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

}
