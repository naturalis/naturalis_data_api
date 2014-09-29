package nl.naturalis.nda.elasticsearch.dao.dao;

import org.elasticsearch.client.Client;

public class BioportalMultiMediaObjectDao extends AbstractDao {

	public BioportalMultiMediaObjectDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}

}
