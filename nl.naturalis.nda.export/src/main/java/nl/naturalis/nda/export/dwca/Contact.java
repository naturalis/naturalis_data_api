package nl.naturalis.nda.export.dwca;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contact")
public class Contact
{
	@XmlElement(name="organisation")
	private String organisation;
	@XmlElement(name="role")
	Role role;
	@XmlElement(name="IndividualName")
	IndividualName individualName;
	@XmlElement(name="address")
	Address address;
	@XmlElement(name="phone")
	private String phone;
	@XmlElement(name="EmailAddress")
	private String emailAddress;
	@XmlElement(name="OnlineUrl")
	private String onlineUrl;
	
	public Contact()
	{
		// TODO Auto-generated constructor stub
	}

	public String getOrganisation()
	{
		return organisation;
	}

	public void setOrganisation(String organisation)
	{
		this.organisation = organisation;
	}

	public Role getRole()
	{
		return role;
	}

	public void setRole(Role role)
	{
		this.role = role;
	}

	public IndividualName getIndividualName()
	{
		return individualName;
	}

	public void setIndividualName(IndividualName individualName)
	{
		this.individualName = individualName;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getEmailAddress()
	{
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getOnlineUrl()
	{
		return onlineUrl;
	}

	public void setOnlineUrl(String onlineUrl)
	{
		this.onlineUrl = onlineUrl;
	}

	public Address getAddress()
	{
		return address;
	}

	public void setAddress(Address address)
	{
		this.address = address;
	}


}
