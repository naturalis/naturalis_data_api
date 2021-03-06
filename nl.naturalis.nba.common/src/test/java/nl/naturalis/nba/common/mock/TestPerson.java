package nl.naturalis.nba.common.mock;

import java.time.OffsetDateTime;
import java.util.List;

import nl.naturalis.nba.api.annotations.NotStored;
import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * A simple class that can be used to create test objects.
 * 
 * @author Ayco Holleman
 *
 */
public class TestPerson implements IDocumentObject {

	@NotStored
	private String id;
	private String firstName;
	private String lastName;
	private OffsetDateTime birthDate;
	private int numChildren;
	private float height;
	private boolean smoker;
	private Address address;
	private String[] hobbies;
	private Pet[] pets;
	private List<Integer> luckyNumbers;
	private List<Address> addressBook;

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

	public OffsetDateTime getBirthDate()
	{
		return birthDate;
	}

	public void setBirthDate(OffsetDateTime birthDate)
	{
		this.birthDate = birthDate;
	}

	public int getNumChildren()
	{
		return numChildren;
	}

	public void setNumChildren(int numChildren)
	{
		this.numChildren = numChildren;
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

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}

	public String[] getHobbies()
	{
		return hobbies;
	}

	public void setHobbies(String[] hobbies)
	{
		this.hobbies = hobbies;
	}

	public Pet[] getPets()
	{
		return pets;
	}

	public void setPets(Pet[] pets)
	{
		this.pets = pets;
	}

	public List<Integer> getLuckyNumbers()
	{
		return luckyNumbers;
	}

	public void setLuckyNumbers(List<Integer> luckyNumbers)
	{
		this.luckyNumbers = luckyNumbers;
	}

	public List<Address> getAddressBook()
	{
		return addressBook;
	}

	public void setAddressBook(List<Address> addressBook)
	{
		this.addressBook = addressBook;
	}

}
