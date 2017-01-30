package nl.naturalis.nba.dao.translate.query;

import nl.naturalis.nba.api.annotations.Analyzer;
import nl.naturalis.nba.api.annotations.Analyzers;
import nl.naturalis.nba.api.model.IDocumentObject;

public class LikeTestObject implements IDocumentObject {

	private String id;
	@Analyzers(Analyzer.LIKE)
	private String firstName;
	@Analyzers(Analyzer.LIKE)
	private String lastName;
	@Analyzers({})
	private String address;
	private int age;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(String id)
	{
		this.id = id;
	}

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
