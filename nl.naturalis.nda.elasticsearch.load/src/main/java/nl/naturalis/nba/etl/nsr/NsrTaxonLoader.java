package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_NSR;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_TAXON;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;

/**
 * The loader component for the NSR taxon import.
 * 
 * @author Ayco Holleman
 *
 */
public class NsrTaxonLoader extends ElasticSearchLoader<ESTaxon> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public NsrTaxonLoader(int treshold, ETLStatistics stats)
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
				return ES_ID_PREFIX_NSR + obj.getSourceSystemId();
			}
		};
	}

}
