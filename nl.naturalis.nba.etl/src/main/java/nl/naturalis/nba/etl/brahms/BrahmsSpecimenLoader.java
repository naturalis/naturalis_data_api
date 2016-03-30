package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_BRAHMS;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;

import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

/**
 * The loader component in the ETL cycle for Brahms specimens.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenLoader extends ElasticSearchLoader<ESSpecimen> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public BrahmsSpecimenLoader(ETLStatistics stats)
	{
		super(indexManager(), LUCENE_TYPE_SPECIMEN, 1000, stats);
	}

	@Override
	protected IdGenerator<ESSpecimen> getIdGenerator()
	{
		return new IdGenerator<ESSpecimen>() {
			@Override
			public String getId(ESSpecimen obj)
			{
				return ES_ID_PREFIX_BRAHMS + obj.getUnitID();
			}
		};
	}

}
