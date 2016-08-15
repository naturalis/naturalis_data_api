package nl.naturalis.nba.dao.es.format.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class modeling the &lt;archive&gt; (root) element within the
 * meta&#46;xml file.
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
	Core core;
	@XmlElement(name = "extension")
	List<Extension> extensions;

	public void addExtension(Extension extension)
	{
		if (extensions == null)
			extensions = new ArrayList<>(4);
		extensions.add(extension);
	}

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

	public String getXmltargetNamespace()
	{
		return xmltargetNamespace;
	}

	public void setXmltargetNamespace(String xmltargetNamespace)
	{
		this.xmltargetNamespace = xmltargetNamespace;
	}

	public Core getCore()
	{
		return core;
	}

	public void setCore(Core core)
	{
		this.core = core;
	}

	public List<Extension> getExtensions()
	{
		return extensions;
	}

	public void setExtensions(List<Extension> extensions)
	{
		this.extensions = extensions;
	}

}
