package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.utils.IOUtil;

public class TestDwcaQuery {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/dwca.zip");
		QuerySpec querySpec = new QuerySpec();
		querySpec.addCondition(new QueryCondition("defaultClassification.genus", "EQUALS", "Larus"));
		client.dwcaQuery(querySpec, fos);
		IOUtil.close(fos);
	}

}
