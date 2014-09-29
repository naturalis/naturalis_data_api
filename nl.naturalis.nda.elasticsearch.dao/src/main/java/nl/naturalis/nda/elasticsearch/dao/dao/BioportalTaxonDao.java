package nl.naturalis.nda.elasticsearch.dao.dao;

import org.elasticsearch.client.Client;

public class BioportalTaxonDao extends AbstractDao {

	public BioportalTaxonDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}

}
