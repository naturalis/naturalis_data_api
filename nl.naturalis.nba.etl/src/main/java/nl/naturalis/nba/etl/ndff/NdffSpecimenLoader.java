package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

public class NdffSpecimenLoader extends Loader<Specimen> {

	public NdffSpecimenLoader(ETLStatistics stats, int treshold)
	{
		super(SPECIMEN, treshold, stats);
	}

	@Override
	protected IdGenerator<Specimen> getIdGenerator()
	{
		return new IdGenerator<Specimen>() {

			@Override
			public String getId(Specimen obj)
			{
				return getElasticsearchId(CRS, obj.getUnitID());
			}
		};
	}

}
