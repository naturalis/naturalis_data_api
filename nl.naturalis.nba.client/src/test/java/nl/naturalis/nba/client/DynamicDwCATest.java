package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class DynamicDwCATest {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/java-client-dwca.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		QuerySpec querySpec = new QuerySpec();
		querySpec.addCondition(new Condition("sourceSystem.code", "=", "NSR"));
		client.dwcaQuery(querySpec, zos);
	}

}
