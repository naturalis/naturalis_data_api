package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSetEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
public class TaxonReferenceExtension extends Extension {

	private static final String ROW_TYPE = "http://rs.gbif.org/terms/1.0/Reference";
	private static final String LOCATION = "reference.txt";

	public TaxonReferenceExtension(DataSetEntity entity)
	{
		super(entity, ROW_TYPE, LOCATION);
	}

}
