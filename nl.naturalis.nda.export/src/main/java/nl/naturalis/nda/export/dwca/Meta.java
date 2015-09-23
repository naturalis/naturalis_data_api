package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <h1>META</h1>
 *  Description: Methods what is used in the DwCAExporter class methods<br>
 *               public void exportDwca(String zipFileName, String namecollectiontype, String totalsize) throws Exception
 *               private static void dwcaObjectToXML(Meta meta) 
 *               
 *  @version	 1.0
 *  @author 	 Reinier.Kartowikromo 
 *  @since		 12-02-2015
 *  
 * */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
public class Meta
{
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
     * @return
     */
	public String getMetadata()
	{
		return metadata;
	}

	/**
	 * 
	 * @param metadata
	 */
	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	/**
	 * 
	 * @return
	 */
	public String getXmlnsxsi()
	{
		return xmlnsxsi;
	}

	/**
	 * 
	 * @param xmlnsxsi
	 */
	public void setXmlnsxsi(String xmlnsxsi)
	{
		this.xmlnsxsi = xmlnsxsi;
	}

	/**
	 * 
	 * @return
	 */
	public String getXmlnstdwg()
	{
		return xmlnstdwg;
	}

	/**
	 * 
	 * @param xmlnstdwg
	 */
	public void setXmlnstdwg(String xmlnstdwg)
	{
		this.xmlnstdwg = xmlnstdwg;
	}

	/**
	 * 
	 * @return
	 */
	public List<Core> getCores()
	{
		return cores;
	}

	/**
	 * 
	 * @param cores
	 */
	public void setCores(List<Core> cores)
	{
		this.cores = cores;
	}

	/**
	 * 
	 * @param cores
	 */
	public void add(Core cores)
	{
		if (this.cores == null)
		{
			this.cores = new ArrayList<>();
		}
		this.cores.add(cores);
	}

    /**
     * 
     * @return
     */
	public String getXmltargetNamespace() 
	{
		return xmltargetNamespace;
	}

    /**
     * 
     * @param xmltargetNamespace
     */
	public void setXmltargetNamespace(String xmltargetNamespace) 
	{
		this.xmltargetNamespace = xmltargetNamespace;
	}
	



}
