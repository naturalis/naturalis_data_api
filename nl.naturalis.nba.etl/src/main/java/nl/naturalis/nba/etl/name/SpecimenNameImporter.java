package nl.naturalis.nba.etl.name;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;

import static nl.naturalis.nba.dao.DocumentType.*;

class SpecimenNameImporter extends NameImporter<Specimen> {

	SpecimenNameImporter()
	{
		super(SPECIMEN);
	}

	@Override
	AbstractNameTransformer<Specimen> createTransformer(ETLStatistics stats)
	{
		return new SpecimenNameTransformer(stats);
	}

}
