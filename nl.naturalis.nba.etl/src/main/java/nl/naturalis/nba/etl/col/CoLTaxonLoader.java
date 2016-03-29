package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.etl.LoadConstants.*;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_TAXON;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

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
public class CoLTaxonLoader extends ElasticSearchLoader<ESTaxon> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public CoLTaxonLoader(ETLStatistics stats, int treshold)
	{
		super(indexManager(), LUCENE_TYPE_TAXON, treshold, stats);
	}

	@Override
	protected IdGenerator<ESTaxon> getIdGenerator()
	{
		return new IdGenerator<ESTaxon>() {
			@Override
			public String getId(ESTaxon obj)
			{
				return ES_ID_PREFIX_COL + obj.getSourceSystemId();
			}
		};
	}

}
