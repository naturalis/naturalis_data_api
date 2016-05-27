package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.dao.es.types.ESType;

class Dummy02 implements ESType {

	private String pet;
	private String color;
	private boolean talks;
	private String firstName;
	private String lastName;

	public String getPet()
	{
		return pet;
	}

	public void setPet(String pet)
	{
		this.pet = pet;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public boolean isTalks()
	{
		return talks;
	}

	public void setTalks(boolean talks)
	{
		this.talks = talks;
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

}
