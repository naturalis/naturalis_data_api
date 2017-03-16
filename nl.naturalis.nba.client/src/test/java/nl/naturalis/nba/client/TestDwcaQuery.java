package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.utils.IOUtil;

public class TestDwcaQuery {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/tom/tmp/dwca.zip");
		QuerySpec querySpec = new QuerySpec();
		querySpec.addCondition(new QueryCondition("defaultClassification.genus", "EQUALS", "Tulipa"));
		client.dwcaQuery(querySpec, fos);
		IOUtil.close(fos);
	}

}
