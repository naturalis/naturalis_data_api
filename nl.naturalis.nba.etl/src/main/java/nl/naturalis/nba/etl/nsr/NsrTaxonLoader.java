package nl.naturalis.nba.etl.nsr;

import static nl.naturalis.nba.api.model.SourceSystem.NSR;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.ESUtil.getElasticsearchId;

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

	public NsrTaxonLoader(int treshold, ETLStatistics stats)
	{
		super(TAXON, treshold, stats);
	}

	@Override
	protected IdGenerator<Taxon> getIdGenerator()
	{
		return new IdGenerator<Taxon>() {

			@Override
			public String getId(Taxon obj)
			{
				return getElasticsearchId(NSR, obj.getSourceSystemId());
			}
		};
	}

}
