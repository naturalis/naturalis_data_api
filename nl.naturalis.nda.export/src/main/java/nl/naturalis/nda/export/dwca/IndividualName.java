package nl.naturalis.nda.export.dwca;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="individualname")
@XmlType(propOrder = { "givenname", "surname"})
public class IndividualName
{
    private String givenname;
    private String surname;
    
    
	public IndividualName()
	{
		// TODO Auto-generated constructor stub
	}


	public String getGivenname()
	{
		return givenname;
	}

	@XmlElement( name = "givenname" )
	public void setGivenname(String givenname)
	{
		this.givenname = givenname;
	}


	public String getSurname()
	{
		return surname;
	}

	@XmlElement( name = "surname" )
	public void setSurname(String surname)
	{
		this.surname = surname;
	}

}
