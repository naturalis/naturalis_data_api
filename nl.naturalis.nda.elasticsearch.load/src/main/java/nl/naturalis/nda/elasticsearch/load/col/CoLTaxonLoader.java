package nl.naturalis.nda.elasticsearch.load.col;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.ElasticSearchLoader;
import static nl.naturalis.nda.elasticsearch.load.LoadConstants.*;

/**
 * @author Ayco Holleman
 *
 */
public class CoLTaxonLoader extends ElasticSearchLoader<ESTaxon> {

	public CoLTaxonLoader(IndexNative indexManager)
	{
		super(indexManager, LUCENE_TYPE_TAXON, 1000);

	}

	@Override
	protected IdGenerator<ESTaxon> getIdGenerator()
	{
		return new IdGenerator<ESTaxon>() {
			@Override
			public String getId(ESTaxon obj)
			{
				return ES_ID_PREFIX_COL + obj.getSourceSystemId();
			}
		};
	}

}
