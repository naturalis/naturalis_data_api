package nl.naturalis.nba.dao.es.format.dwca;

import nl.naturalis.nba.dao.es.format.IDataSetField;

public class TaxonMetaXmlGenerator extends MetaXmlGenerator {

	public TaxonMetaXmlGenerator(IDataSetField[] fields)
	{
		super(fields);
	}

	String getLocation()
	{
		return "taxa.txt";
	}

	String getRowType()
	{
		return "http://rs.tdwg.org/dwc/terms/Occurrence";
	}

}
