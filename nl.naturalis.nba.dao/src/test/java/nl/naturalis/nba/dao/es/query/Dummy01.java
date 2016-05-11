package nl.naturalis.nba.dao.es.query;

import nl.naturalis.nba.dao.es.types.ESType;

class Dummy01 implements ESType {

	private String firstName;
	private String lastName;
	private boolean hasChildren;
	private String favoritePet;

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

	public boolean isHasChildren()
	{
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren)
	{
		this.hasChildren = hasChildren;
	}

	public String getFavoritePet()
	{
		return favoritePet;
	}

	public void setFavoritePet(String favoritePet)
	{
		this.favoritePet = favoritePet;
	}

}
