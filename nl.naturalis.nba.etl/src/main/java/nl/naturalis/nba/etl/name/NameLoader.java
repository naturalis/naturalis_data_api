package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

class NameLoader extends Loader<NameGroup> {

	NameLoader(int treshold, ETLStatistics stats)
	{
		super(NAME_GROUP, treshold, stats);
	}

	@Override
	protected IdGenerator<NameGroup> getIdGenerator()
	{
		return new IdGenerator<NameGroup>() {

			@Override
			public String getId(NameGroup obj)
			{
				return obj.getName();
			}
		};
	}

}
