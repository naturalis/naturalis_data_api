package nl.naturalis.nba.dao.es.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Models the &lt;id&gt; element of a meta XML file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "id")
public class Id {

	@XmlAttribute(name = "index")
	private int index;

	public Id()
	{
	}

	/**
	 * 
	 * @return result index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * 
	 * @param index
	 *            set value index
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}

}
