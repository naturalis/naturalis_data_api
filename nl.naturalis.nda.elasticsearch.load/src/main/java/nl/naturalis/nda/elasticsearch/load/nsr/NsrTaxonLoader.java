package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_NSR;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_TAXON;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

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
