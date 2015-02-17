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
	@XmlAttribute(name = "xmlns")
	private String xmlns;
	@XmlAttribute(name = "xmlns:eml")
	private String emlxmlns;
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

}
