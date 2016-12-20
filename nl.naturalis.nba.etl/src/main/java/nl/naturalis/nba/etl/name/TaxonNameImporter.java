package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.ETLStatistics;

class TaxonNameImporter extends NameImporter<Taxon> {

	TaxonNameImporter()
	{
		super(TAXON);
	}

	@Override
	AbstractNameTransformer<Taxon> createTransformer(ETLStatistics stats, NameLoader loader)
	{
		return new TaxonNameTransformer(stats, loader);
	}

}
