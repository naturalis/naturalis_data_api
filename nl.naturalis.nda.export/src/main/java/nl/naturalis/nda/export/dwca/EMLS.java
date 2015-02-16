package nl.naturalis.nda.export.dwca;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "eml")
public class EMLS
{

	private String emlxmlns;
	
	List<Datasets> datasets;

	public List<Datasets> getDatasets()
	{
		return datasets;
	}

	@XmlElement(name = "dataset")
	public void setDatasets(List<Datasets> datasets)
	{
		this.datasets = datasets;
	}

	public void add(Datasets dataset)
	{
		if (this.datasets == null)
		{
			this.datasets = new ArrayList<Datasets>();
		}
		this.datasets.add(dataset);

	}

	public String getEmlxmlns()
	{
		return emlxmlns;
	}

	@XmlAttribute(name = "xmlns:eml")
	public void setEmlxmlns(String emlxmlns)
	{
		this.emlxmlns = emlxmlns;
	}

}
