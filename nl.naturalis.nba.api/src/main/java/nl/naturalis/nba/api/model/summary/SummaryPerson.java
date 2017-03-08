package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;

public class SummaryPerson implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String fullName;
	private SummaryOrganization organization;

	public SummaryPerson()
	{
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public SummaryOrganization getOrganization()
	{
		return organization;
	}

	public void setOrganization(SummaryOrganization organization)
	{
		this.organization = organization;
	}
}
