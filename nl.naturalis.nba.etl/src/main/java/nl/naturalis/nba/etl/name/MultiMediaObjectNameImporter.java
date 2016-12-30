package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.etl.ETLStatistics;

class MultiMediaObjectNameImporter extends NameImporter<MultiMediaObject> {

	MultiMediaObjectNameImporter()
	{
		super(MULTI_MEDIA_OBJECT);
	}

	@Override
	AbstractNameTransformer<MultiMediaObject> createTransformer(ETLStatistics stats)
	{
		return new MultiMediaObjectNameTransformer(stats);
	}

}
