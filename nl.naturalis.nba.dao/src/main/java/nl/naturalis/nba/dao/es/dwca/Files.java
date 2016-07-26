package nl.naturalis.nba.dao.es.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Models the &lt;files&gt; element of a meta XML file
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "files")
public class Files {

	@XmlElement(name = "location")
	private String location;

	public Files()
	{
	}

	/**
	 * 
	 * @return value location
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * 
	 * @param location
	 *            set location
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

}
