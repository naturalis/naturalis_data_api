package nl.naturalis.nda.export.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**  
 * <h1>FILES</h1>
 *  Description: Class to create the files- and location element under the core element of the Meta.xml file.
 * 
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *   
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "files")
public class Files
{
	@XmlElement(name="location")
    private String location;
	
	public Files()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @return
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * 
	 * @param location
	 */
	public void setLocation(String location)
	{
		this.location = location;
	}

}
