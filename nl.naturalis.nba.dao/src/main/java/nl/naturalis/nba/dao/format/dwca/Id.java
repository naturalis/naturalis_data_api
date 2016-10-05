package nl.naturalis.nba.dao.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;id&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "id")
class Id {

	@XmlAttribute(name = "index")
	private int index;

	Id()
	{
	}

	int getIndex()
	{
		return index;
	}

	void setIndex(int index)
	{
		this.index = index;
	}

}
