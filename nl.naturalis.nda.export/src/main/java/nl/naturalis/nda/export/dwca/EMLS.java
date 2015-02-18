package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "eml")
public class EMLS
{
	@XmlAttribute(name = "xmlns:eml")
	private String emlxmlns;
	@XmlAttribute(name = "xmlns:md")
	private String xmlnsmd;
	@XmlAttribute(name="xmlns:proj")
	private String xmlnsproj;
	@XmlAttribute(name= "xmlns:d")
	private String xmlnsd;
	@XmlAttribute(name="xmlns:res")
	private String xmlnsres;
	@XmlAttribute(name="xmlns:dc")
	private String xmlnsdc;
	@XmlAttribute(name="xmlns:xsi")
	private String xmlnsxsi;
	@XmlAttribute(name="xsi:schemaLocation")
	private String xsischemaLocation;
	@XmlAttribute(name="packageId")
    private String packageId;	
	@XmlAttribute(name="system")
	private String system;
	@XmlAttribute(name="scope")
	private String scope;
	@XmlAttribute(name="xml:lang")
	private String xmllang;
	@XmlAttribute(name = "xmlns")
	private String xmlns;
	
	@XmlElement(name = "dataset")
	List<Dataset> datasets;

	public List<Dataset> getDatasets()
	{
		return datasets;
	}

	
	public void setDatasets(List<Dataset> datasets)
	{
		this.datasets = datasets;
	}

	public void add(Dataset dataset)
	{
		if (this.datasets == null)
		{
			this.datasets = new ArrayList<Dataset>();
		}
		this.datasets.add(dataset);

	}

	public String getEmlxmlns()
	{
		return emlxmlns;
	}

	
	public void setEmlxmlns(String emlxmlns)
	{
		this.emlxmlns = emlxmlns;
	}

	public String getXmlns()
	{
		return xmlns;
	}

	public void setXmlns(String xmlns)
	{
		this.xmlns = xmlns;
	}


	public String getXmlnsmd()
	{
		return xmlnsmd;
	}


	public void setXmlnsmd(String xmlnsmd)
	{
		this.xmlnsmd = xmlnsmd;
	}


	public String getXmlnsproj()
	{
		return xmlnsproj;
	}


	public void setXmlnsproj(String xmlnsproj)
	{
		this.xmlnsproj = xmlnsproj;
	}


	public String getXmlnsd()
	{
		return xmlnsd;
	}


	public void setXmlnsd(String xmlnsd)
	{
		this.xmlnsd = xmlnsd;
	}


	public String getXmlnsres()
	{
		return xmlnsres;
	}


	public void setXmlnsres(String xmlnsres)
	{
		this.xmlnsres = xmlnsres;
	}


	public String getXmlnsdc()
	{
		return xmlnsdc;
	}


	public void setXmlnsdc(String xmlnsdc)
	{
		this.xmlnsdc = xmlnsdc;
	}


	public String getXmlnsxsi()
	{
		return xmlnsxsi;
	}


	public void setXmlnsxsi(String xmlnsxsi)
	{
		this.xmlnsxsi = xmlnsxsi;
	}


	public String getXsischemaLocation()
	{
		return xsischemaLocation;
	}


	public void setXsischemaLocation(String xsischemaLocation)
	{
		this.xsischemaLocation = xsischemaLocation;
	}


	public String getPackageId()
	{
		return packageId;
	}


	public void setPackageId(String packageId)
	{
		this.packageId = packageId;
	}


	public String getSystem()
	{
		return system;
	}


	public void setSystem(String system)
	{
		this.system = system;
	}


	public String getScope()
	{
		return scope;
	}


	public void setScope(String scope)
	{
		this.scope = scope;
	}


	public String getXmllang()
	{
		return xmllang;
	}


	public void setXmllang(String xmllang)
	{
		this.xmllang = xmllang;
	}

}
