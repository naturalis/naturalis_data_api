package nl.naturalis.nba.dao.es.format.dwca;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.Entity;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "extension")
class TaxonReferenceExtension extends Extension {

	private static final String ROW_TYPE = "http://rs.gbif.org/terms/1.0/Reference";
	private static final String LOCATION = "reference.txt";

	TaxonReferenceExtension()
	{
		super();
		this.rowType = ROW_TYPE;
		this.files = new Files(LOCATION);
	}

	TaxonReferenceExtension forDataSet(DataSet ds)
	{
		Entity entity = ds.getCollectionConfiguration().getEntityConfiguration("reference");
		this.fields = DwcaUtil.getMetaXmlFieldElements(entity);
		return this;
	}
}
