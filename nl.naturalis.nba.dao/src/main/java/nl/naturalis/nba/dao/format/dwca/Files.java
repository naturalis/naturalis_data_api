package nl.naturalis.nba.dao.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;files&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "files")
class Files {

	@XmlElement(name = "location")
	private String location;

	Files()
	{
	}

	Files(String location)
	{
		this.location = location;
	}

	String getLocation()
	{
		return location;
	}

	void setLocation(String location)
	{
		this.location = location;
	}

}
