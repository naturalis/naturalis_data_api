package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.api.model.SourceSystem.CRS;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.getElasticsearchId;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the CRS specimen import.
 * 
 * @author Ayco Holleman
 *
 */
class CrsSpecimenLoader extends Loader<Specimen> {

	CrsSpecimenLoader(int queueSize, ETLStatistics stats)
	{
		super(SPECIMEN, queueSize, stats);
	}

}
