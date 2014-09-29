package nl.naturalis.nda.elasticsearch.dao.dao;

import org.elasticsearch.client.Client;

public class BioportalSpecimenDao extends AbstractDao {

	public BioportalSpecimenDao(Client esClient, String ndaIndexName)
	{
		super(esClient, ndaIndexName);
	}

}
