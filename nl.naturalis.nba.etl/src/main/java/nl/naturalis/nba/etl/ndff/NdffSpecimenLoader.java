package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.etl.LoadConstants.ES_ID_PREFIX_NDFF;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ElasticSearchLoader;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

public class NdffSpecimenLoader extends ElasticSearchLoader<ESSpecimen> {

	private static IndexManagerNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public NdffSpecimenLoader(ETLStatistics stats, int treshold)
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
				return ES_ID_PREFIX_NDFF + obj.getUnitID();
			}
		};
	}

}
