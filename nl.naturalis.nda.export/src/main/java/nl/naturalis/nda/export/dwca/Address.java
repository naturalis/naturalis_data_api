package nl.naturalis.nda.export.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="address")
public class Address
{
	@XmlElement(name="deliverypoint")
	private String deliveryPoint;
	@XmlElement(name="city")
	private String City;
	@XmlElement(name="stateprovince")
	private String stateProvince;
	@XmlElement(name="country")
	private String Country;
	@XmlElement(name="postalcode")
	private String postalCode;

	public Address()
	{
		// TODO Auto-generated constructor stub
	}

	public String getDeliveryPoint()
	{
		return deliveryPoint;
	}

	public void setDeliveryPoint(String deliveryPoint)
	{
		this.deliveryPoint = deliveryPoint;
	}

	public String getCity()
	{
		return City;
	}

	public void setCity(String city)
	{
		this.City = city;
	}

	public String getStateProvince()
	{
		return stateProvince;
	}

	public void setStateProvince(String stateProvince)
	{
		this.stateProvince = stateProvince;
	}

	public String getCountry()
	{
		return Country;
	}

	public void setCountry(String country)
	{
		this.Country = country;
	}

	public String getPostalCode()
	{
		return postalCode;
	}

	public void setPostalCode(String postalCode)
	{
		this.postalCode = postalCode;
	}

}
