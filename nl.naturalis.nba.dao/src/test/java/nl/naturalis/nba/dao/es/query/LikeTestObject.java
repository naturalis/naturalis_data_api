package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.dao.es.types.ESType;

public class LikeTestObject implements ESType {

	@Analyzers(Analyzer.LIKE)
	private String firstName;
	@Analyzers(Analyzer.LIKE)
	private String lastName;
	@Analyzers({})
	private String address;
	private int age;

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

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

}
