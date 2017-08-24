package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component in the ETL cycle for NDFF specimens.
 * 
 * @author Ayco Holleman
 *
 */
public class NdffSpecimenLoader extends Loader<Specimen> {

	public NdffSpecimenLoader(ETLStatistics stats, int treshold)
	{
		super(SPECIMEN, treshold, stats);
	}

}
