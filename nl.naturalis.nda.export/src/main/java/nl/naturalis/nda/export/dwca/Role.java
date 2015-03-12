package nl.naturalis.nda.export.dwca;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="role")
public class Role
{
	private String position;
	
	public Role()
	{
		// TODO Auto-generated constructor stub
	}

	
	public String getPosition()
	{
		return position;
	}

	@XmlElement( name = "position" )
	public void setPosition(String position)
	{
		this.position = position;
	}

}
