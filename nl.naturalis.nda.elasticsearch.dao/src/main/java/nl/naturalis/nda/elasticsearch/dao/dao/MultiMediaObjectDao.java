package nl.naturalis.nda.elasticsearch.dao.dao;

import org.elasticsearch.client.Client;

public class MultiMediaObjectDao extends AbstractDao {

	public MultiMediaObjectDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}

}
