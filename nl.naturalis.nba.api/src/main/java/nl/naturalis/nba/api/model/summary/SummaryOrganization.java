package nl.naturalis.nba.api.model.summary;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import com.fasterxml.jackson.annotation.JsonCreator;

import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.INbaModelObject;
import nl.naturalis.nba.api.model.Organization;

/**
 * A miniature version of {@link Organization}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryOrganization implements INbaModelObject {

	@Analyzers({ CASE_INSENSITIVE, DEFAULT, LIKE })
	private String name;

	@JsonCreator
	public SummaryOrganization(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

}
