package nl.naturalis.nba.dao.es.test;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.annotations.NotIndexed;
import nl.naturalis.nba.dao.common.test.Country;

/**
 * A simple class that can be used to create test objects.
 * 
 * @author Ayco Holleman
 *
 */
public class Address {

	@Analyzers({ Analyzer.CASE_INSENSITIVE, Analyzer.LIKE, Analyzer.DEFAULT })
	private String street;
	@NotIndexed
	private int number;
	@Analyzers({ Analyzer.CASE_INSENSITIVE })
	private String postalCode;
	@Analyzers({ Analyzer.CASE_INSENSITIVE, Analyzer.LIKE, Analyzer.DEFAULT })
	private String city;
	private Country country;

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public Country getCountry()
	{
		return country;
	}

	public void setCountry(Country country)
	{
		this.country = country;
	}

}
