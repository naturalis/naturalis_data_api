package nl.naturalis.nba.dao.es.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Models de &lt;archive&gt; element of the meta XML file
 * 
 * @version 1.0
 * @author Reinier.Kartowikromo
 * @since 12-02-2015
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
public class Archive {

	@XmlAttribute(name = "metadata")
	private String metadata;
	@XmlAttribute(name = "xmlns:xsi")
	private String xmlnsxsi;
	@XmlAttribute(name = "xmlns")
	private String xmlnstdwg;
	@XmlAttribute(name = "targetNamespace")
	private String xmltargetNamespace;
	@XmlElement(name = "core")
	List<Core> cores;

	/**
	 * 
	 * @return metadata
	 */
	public String getMetadata()
	{
		return metadata;
	}

	/**
	 * 
	 * @param metadata
	 *            set value metadata
	 */
	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * 
	 * @return xmlnsxsi
	 */
	public String getXmlnsxsi()
	{
		return xmlnsxsi;
	}

	/**
	 * 
	 * @param xmlnsxsi
	 *            set value xmlnsxsi
	 */
	public void setXmlnsxsi(String xmlnsxsi)
	{
		this.xmlnsxsi = xmlnsxsi;
	}

	/**
	 * 
	 * @return result xmlnstdwg
	 */
	public String getXmlnstdwg()
	{
		return xmlnstdwg;
	}

	/**
	 * 
	 * @param xmlnstdwg
	 *            set value xmlnstdwg
	 */
	public void setXmlnstdwg(String xmlnstdwg)
	{
		this.xmlnstdwg = xmlnstdwg;
	}

	/**
	 * 
	 * @return cores
	 */
	public List<Core> getCores()
	{
		return cores;
	}

	/**
	 * 
	 * @param cores
	 *            set cores
	 */
	public void setCores(List<Core> cores)
	{
		this.cores = cores;
	}

	/**
	 * 
	 * @param cores
	 *            add cores
	 */
	public void add(Core cores)
	{
		if (this.cores == null) {
			this.cores = new ArrayList<>();
		}
		this.cores.add(cores);
	}

	/**
	 * 
	 * @return xmltargetNamespace
	 */
	public String getXmltargetNamespace()
	{
		return xmltargetNamespace;
	}

	/**
	 * 
	 * @param xmltargetNamespace
	 *            set xmltargetNamespace
	 */
	public void setXmltargetNamespace(String xmltargetNamespace)
	{
		this.xmltargetNamespace = xmltargetNamespace;
	}

}
