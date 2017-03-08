package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;

import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class NameLoader extends Loader<ScientificNameGroup> {

	NameLoader(int treshold, ETLStatistics stats)
	{
		super(SCIENTIFIC_NAME_GROUP, treshold, stats);
	}

	@Override
	protected IdGenerator<ScientificNameGroup> getIdGenerator()
	{
		return new IdGenerator<ScientificNameGroup>() {

			@Override
			public String getId(ScientificNameGroup obj)
			{
				return obj.getName();
			}
		};
	}

}
