package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_CRS;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

/**
 * The loader component for the CRS specimen import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsSpecimenLoader extends ElasticSearchLoader<ESSpecimen> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	CrsSpecimenLoader(ETLStatistics stats, int treshold)
	{
		super(indexManager(), LUCENE_TYPE_SPECIMEN, treshold, stats);
	}

	@Override
	protected IdGenerator<ESSpecimen> getIdGenerator()
	{
		return new IdGenerator<ESSpecimen>() {
			@Override
			public String getId(ESSpecimen obj)
			{
				return ES_ID_PREFIX_CRS + obj.getUnitID();
			}
		};
	}

}
