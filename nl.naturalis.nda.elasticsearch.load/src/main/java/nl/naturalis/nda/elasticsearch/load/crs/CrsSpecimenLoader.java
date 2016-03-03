package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_CRS;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nda.elasticsearch.client.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

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
