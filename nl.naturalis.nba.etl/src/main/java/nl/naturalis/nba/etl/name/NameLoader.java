package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME;

import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

public class NameLoader extends Loader<Name> {

	public NameLoader(int treshold, ETLStatistics stats)
	{
		super(NAME, treshold, stats);
	}

	@Override
	protected IdGenerator<Name> getIdGenerator()
	{
		return new IdGenerator<Name>() {

			@Override
			public String getId(Name obj)
			{
				return obj.getName();
			}
		};
	}

}
