package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component in the ETL cycle for Brahms specimens.
 * 
 * @author Ayco Holleman
 *
 */
class BrahmsSpecimenLoader extends Loader<Specimen> {

//	private static final IdGenerator<Specimen> ID_GENERATOR = new IdGenerator<Specimen>() {
//
//		@Override
//		public String getId(Specimen obj)
//		{
//			return getElasticsearchId(BRAHMS, obj.getUnitID());
//		}
//	};

	public BrahmsSpecimenLoader(int queueSize, ETLStatistics stats)
	{
		super(SPECIMEN, queueSize, stats);
	}

//	@Override
//	protected IdGenerator<Specimen> getIdGenerator()
//	{
//		return ID_GENERATOR;
//	}

}
