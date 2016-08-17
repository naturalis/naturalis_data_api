package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSetEntity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
public class TaxonVernacularNameExtension extends Extension {

	private static final String ROW_TYPE = "http://rs.gbif.org/terms/1.0/TaxonReferenceExtension.java";
	private static final String LOCATION = "vernacular.txt";

	public TaxonVernacularNameExtension(DataSetEntity entity)
	{
		super(entity, ROW_TYPE, LOCATION);
	}

}
