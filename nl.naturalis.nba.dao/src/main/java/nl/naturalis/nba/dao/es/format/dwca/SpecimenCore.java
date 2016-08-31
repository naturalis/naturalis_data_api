package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSetConfiguration;
import nl.naturalis.nba.dao.es.format.EntityConfiguration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "core")
class SpecimenCore extends Core {

	private static final String ROW_TYPE = "http://rs.tdwg.org/dwc/terms/Occurrence";
	private static final String LOCATION = "occurrence.txt";

	SpecimenCore()
	{
		super();
		this.rowType = ROW_TYPE;
		this.files = new Files(LOCATION);
	}

	SpecimenCore forDataSet(DataSetConfiguration ds)
	{
//		EntityConfiguration entity = ds.getCollectionConfiguration().getEntity("occurrence");
//		this.fields = DwcaUtil.getMetaXmlFieldElements(entity);
		return this;
	}
}
