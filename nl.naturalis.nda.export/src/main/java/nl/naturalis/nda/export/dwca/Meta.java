package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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



	public String getMetadata()
	{
		return metadata;
	}

	
	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}

	public String getXmlnsxsi()
	{
		return xmlnsxsi;
	}

	public void setXmlnsxsi(String xmlnsxsi)
	{
		this.xmlnsxsi = xmlnsxsi;
	}

	public String getXmlnstdwg()
	{
		return xmlnstdwg;
	}

	public void setXmlnstdwg(String xmlnstdwg)
	{
		this.xmlnstdwg = xmlnstdwg;
	}

	public List<Core> getCores()
	{
		return cores;
	}

	public void setCores(List<Core> cores)
	{
		this.cores = cores;
	}

	public void add(Core cores)
	{
		if (this.cores == null)
		{
			this.cores = new ArrayList<Core>();
		}
		this.cores.add(cores);

	}


	public String getXmltargetNamespace() 
	{
		return xmltargetNamespace;
	}


	public void setXmltargetNamespace(String xmltargetNamespace) 
	{
		this.xmltargetNamespace = xmltargetNamespace;
	}
	



}
