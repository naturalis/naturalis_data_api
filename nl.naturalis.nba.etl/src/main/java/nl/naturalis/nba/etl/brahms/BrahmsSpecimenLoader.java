package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

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

	public BrahmsSpecimenLoader(int queueSize, ETLStatistics stats)
	{
		super(SPECIMEN, queueSize, stats);
	}

}
