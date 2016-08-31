package nl.naturalis.nba.dao.es.format.dwca;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSetConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "archive")
class SpecimenArchive extends Archive {

	SpecimenArchive()
	{
		super();
	}

	SpecimenArchive forDataSet(DataSetConfiguration dataSet)
	{
		this.core = createCore(dataSet);
		this.extensions = createExtensions(dataSet);
		return this;
	}

	private static Core createCore(DataSetConfiguration dataSet)
	{
		return new SpecimenCore().forDataSet(dataSet);
	}

	private static List<Extension> createExtensions(DataSetConfiguration dataSet)
	{
		return null;
	}

}
