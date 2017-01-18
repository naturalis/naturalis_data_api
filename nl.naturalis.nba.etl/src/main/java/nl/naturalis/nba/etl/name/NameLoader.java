package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_SUMMARY;

import nl.naturalis.nba.api.model.ScientificNameSummary;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class NameLoader extends Loader<ScientificNameSummary> {

	NameLoader(int treshold, ETLStatistics stats)
	{
		super(SCIENTIFIC_NAME_SUMMARY, treshold, stats);
	}

	@Override
	protected IdGenerator<ScientificNameSummary> getIdGenerator()
	{
		return new IdGenerator<ScientificNameSummary>() {

			@Override
			public String getId(ScientificNameSummary obj)
			{
				return obj.getFullScientificName();
			}
		};
	}

}
