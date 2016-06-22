package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

public class NdffSpecimenLoader extends Loader<ESSpecimen> {

	public NdffSpecimenLoader(ETLStatistics stats, int treshold)
	{
		super(SPECIMEN, treshold, stats);
	}

	@Override
	protected IdGenerator<ESSpecimen> getIdGenerator()
	{
		return new IdGenerator<ESSpecimen>() {

			@Override
			public String getId(ESSpecimen obj)
			{
				return getElasticsearchId(CRS, obj.getUnitID());
			}
		};
	}

}
