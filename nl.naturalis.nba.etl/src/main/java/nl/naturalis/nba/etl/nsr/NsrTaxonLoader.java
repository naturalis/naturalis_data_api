package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.dao.DocumentType.TAXON;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the NSR taxon import.
 * 
 * @author Ayco Holleman
 *
 */
class NsrTaxonLoader extends Loader<Taxon> {

	public NsrTaxonLoader(int queueSize, ETLStatistics stats)
	{
		super(TAXON, queueSize, stats);
	}

}
