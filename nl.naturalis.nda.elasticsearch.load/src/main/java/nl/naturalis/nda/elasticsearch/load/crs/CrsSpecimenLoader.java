package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.ES_ID_PREFIX_CRS;
import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import nl.naturalis.nda.elasticsearch.load.Registry;

public class CrsSpecimenLoader extends ElasticSearchLoader<ESSpecimen> {

	private static IndexNative indexManager()
	{
		return Registry.getInstance().getNbaIndexManager();
	}

	public CrsSpecimenLoader(ETLStatistics stats, int treshold)
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
