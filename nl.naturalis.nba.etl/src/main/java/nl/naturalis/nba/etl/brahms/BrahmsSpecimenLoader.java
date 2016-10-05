package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.types.ESSpecimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component in the ETL cycle for Brahms specimens.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenLoader extends Loader<ESSpecimen> {

	private static final IdGenerator<ESSpecimen> ID_GENERATOR = new IdGenerator<ESSpecimen>() {

		@Override
		public String getId(ESSpecimen obj)
		{
			return getElasticsearchId(BRAHMS, obj.getUnitID());
		}
	};

	public BrahmsSpecimenLoader(ETLStatistics stats)
	{
		super(SPECIMEN, 1000, stats);
	}

	@Override
	protected IdGenerator<ESSpecimen> getIdGenerator()
	{
		return ID_GENERATOR;
	}

}
