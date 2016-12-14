package nl.naturalis.nba.etl.sn;

import nl.naturalis.nba.api.model.XScientificName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

public class ScientificNameLoader extends Loader<XScientificName> {

	public ScientificNameLoader(DocumentType<XScientificName> type, int treshold,
			ETLStatistics stats)
	{
		super(type, treshold, stats);
	}

	@Override
	protected IdGenerator<XScientificName> getIdGenerator()
	{
		return null;
	}

}
