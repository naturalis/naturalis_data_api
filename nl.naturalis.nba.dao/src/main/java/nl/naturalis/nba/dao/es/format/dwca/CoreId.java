package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;coreid&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "coreid")
class CoreId {

	@XmlAttribute(name = "index")
	private int index;

	CoreId()
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
