package nl.naturalis.nba.dao.es.format.dwca;

import nl.naturalis.nba.dao.es.format.IDataSetField;

public class OccurrenceMetaXmlGenerator extends MetaXmlGenerator {

	public OccurrenceMetaXmlGenerator(IDataSetField[] fields)
	{
		super(fields);
	}

	String getLocation()
	{
		return "occurrence.txt";
	}

	String getRowType()
	{
		return "http://rs.tdwg.org/dwc/terms/Occurrence";
	}

}
