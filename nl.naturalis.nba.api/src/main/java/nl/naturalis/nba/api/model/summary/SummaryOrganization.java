package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;

public class SummaryOrganization implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String name;

	public SummaryOrganization()
	{
	}

	public SummaryOrganization(String name)
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

}
