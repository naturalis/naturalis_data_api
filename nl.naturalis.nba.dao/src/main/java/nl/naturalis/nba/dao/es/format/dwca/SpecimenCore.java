package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSetEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "core")
public class SpecimenCore extends Core {

	private static final String ROW_TYPE = "http://rs.tdwg.org/dwc/terms/Occurrence";
	private static final String LOCATION = "occurrence.txt";

	public SpecimenCore(DataSetEntity entity)
	{
		super(entity, ROW_TYPE, LOCATION);
	}

}
