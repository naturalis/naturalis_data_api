package nl.naturalis.nba.dao.query;

import nl.naturalis.nba.dao.types.ESType;

class EqualsTestObject implements ESType {

	private String firstName;
	private String lastName;
	private int age;
	private boolean married;
	private int numChildren;
	private String favouritePet;
	private String favouriteFood;
	private String country;
	private String city;
	
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
	
	public int getAge()
	{
		return age;
	}
	
	public void setAge(int age)
	{
		this.age = age;
	}
	
	public boolean isMarried()
	{
		return married;
	}
	
	public void setMarried(boolean married)
	{
		this.married = married;
	}
	
	public int getNumChildren()
	{
		return numChildren;
	}
	
	public void setNumChildren(int numChildren)
	{
		this.numChildren = numChildren;
	}
	
	public String getFavouritePet()
	{
		return favouritePet;
	}
	
	public void setFavouritePet(String favouritePet)
	{
		this.favouritePet = favouritePet;
	}
	
	public String getFavouriteFood()
	{
		return favouriteFood;
	}
	
	public void setFavouriteFood(String favouriteFood)
	{
		this.favouriteFood = favouriteFood;
	}
	
	public String getCountry()
	{
		return country;
	}
	
	public void setCountry(String country)
	{
		this.country = country;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public void setCity(String city)
	{
		this.city = city;
	}


}
