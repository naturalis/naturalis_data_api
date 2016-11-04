package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.api.model.SourceSystem.COL;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the CoL import. Loads Taxon documents into
 * ElasticSearch. This class is for all CoL by all CoL importers (
 * {@link CoLTaxonImporter}, {@link CoLSynonymImporter},
 * {@link CoLVernacularNameImporter} and {@link CoLReferenceImporter}). Only the
 * taxon importer (run first) actually creates new documents; the other
 * importers overwrite them with enriched versions.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLTaxonLoader extends Loader<Taxon> {

	public CoLTaxonLoader(ETLStatistics stats, int treshold)
	{
		super(TAXON, treshold, stats);
	}

	@Override
	protected IdGenerator<Taxon> getIdGenerator()
	{
		return new IdGenerator<Taxon>() {

			@Override
			public String getId(Taxon obj)
			{
				return getElasticsearchId(COL, obj.getSourceSystemId());
			}
		};
	}

}
