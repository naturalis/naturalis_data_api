package nl.naturalis.nba.common.mock;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;

/**
 * A simple class that can be used to create test objects.
 * 
 * @author Ayco Holleman
 *
 */
public class Country {

	private String name;
	@Analyzers({ Analyzer.CASE_INSENSITIVE })
	private String isoCode;
	private int dialNumber;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getIsoCode()
	{
		return isoCode;
	}

	public void setIsoCode(String isoCode)
	{
		this.isoCode = isoCode;
	}

	public int getDialNumber()
	{
		return dialNumber;
	}

	public void setDialNumber(int dialNumber)
	{
		this.dialNumber = dialNumber;
	}
}
