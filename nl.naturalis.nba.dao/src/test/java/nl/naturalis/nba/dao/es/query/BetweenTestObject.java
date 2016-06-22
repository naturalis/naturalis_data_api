package nl.naturalis.nba.dao.es.query;

import java.util.Date;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.dao.es.types.ESType;

public class BetweenTestObject implements ESType {

	@Analyzers(Analyzer.LIKE)
	private String firstName;
	@Analyzers(Analyzer.LIKE)
	private String lastName;
	@Analyzers({})
	private String address;
	private Date birthDate;
	private boolean married;
	private int numKids;

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public Date getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	public boolean isMarried()
	{
		return married;
	}

	public void setMarried(boolean married)
	{
		this.married = married;
	}

	public int getNumKids()
	{
		return numKids;
	}

	public void setNumKids(int numKids)
	{
		this.numKids = numKids;
	}

}
