package nl.naturalis.nba.dao.es.format.dwca;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSet;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
class SpecimenArchive extends Archive {

	SpecimenArchive()
	{
		super();
	}

	void setCoreAndExtensions(DataSet dataSet)
	{
		this.core = createCore(dataSet);
		this.extensions = createExtensions(dataSet);
	}

	private static Core createCore(DataSet dataSet)
	{
		return new SpecimenCore().forDataSet(dataSet);
	}

	private static List<Extension> createExtensions(DataSet dataSet)
	{
		return null;
	}

}
