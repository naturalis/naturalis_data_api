package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.es.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the CRS specimen import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsSpecimenLoader extends Loader<ESSpecimen> {

	CrsSpecimenLoader(ETLStatistics stats, int treshold)
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