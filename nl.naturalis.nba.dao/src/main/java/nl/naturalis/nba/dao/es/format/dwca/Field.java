package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;field&gt; element within the meta&#46;xml file.
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Field {

	@XmlAttribute(name = "index")
	private String index;

	@XmlAttribute(name = "term")
	private String term;

	public Field()
	{
	}

	public Field(String index, String Term)
	{
		this.index = index;
		this.term = Term;
	}

	public String getIndex()
	{
		return index;
	}

	public void setIndex(String index)
	{
		this.index = index;
	}

	public String getTerm()
	{
		return term;
	}

	public void setTerm(String term)
	{
		this.term = term;
	}

}
