package nl.naturalis.nba.dao.es.dwca;

import nl.naturalis.nba.dao.es.csv.IColumn;

public class OccurrenceMetaXmlGenerator extends MetaXmlGenerator {

	public OccurrenceMetaXmlGenerator(IColumn[] columns)
	{
		super(columns);
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
