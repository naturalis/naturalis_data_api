package nl.naturalis.nba.dao.es.test;

import java.util.Date;

import nl.naturalis.nba.dao.es.types.ESType;

/**
 * A simple class that can be used to create test objects.
 * 
 * @author Ayco Holleman
 *
 */
public class Person implements ESType {

	private String firstName;
	private String lastName;
	private Date birthDate;
	private int numKids;
	private Address address;
	private Pet[] pets;
	private float height;
	private boolean smoker;

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

	public Date getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
	}

	public int getNumKids()
	{
		return numKids;
	}

	public void setNumKids(int numKids)
	{
		this.numKids = numKids;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public Pet[] getPets()
	{
		return pets;
	}

	public void setPets(Pet[] pets)
	{
		this.pets = pets;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public boolean isSmoker()
	{
		return smoker;
	}

	public void setSmoker(boolean smoker)
	{
		this.smoker = smoker;
	}

}
