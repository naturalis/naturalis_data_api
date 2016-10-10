package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.ESUtil.getElasticsearchId;

import nl.naturalis.nba.dao.types.ESTaxon;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.Loader;

/**
 * The loader component for the NSR taxon import.
 * 
 * @author Ayco Holleman
 *
 */
class NsrTaxonLoader extends Loader<ESTaxon> {

	public NsrTaxonLoader(int treshold, ETLStatistics stats)
	{
		super(TAXON, treshold, stats);
	}

	@Override
	protected IdGenerator<ESTaxon> getIdGenerator()
	{
		return new IdGenerator<ESTaxon>() {

			@Override
			public String getId(ESTaxon obj)
			{
				return getElasticsearchId(NSR, obj.getSourceSystemId());
			}
		};
	}

}
