package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;

public class StaticDwCATest {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException, NoSuchDataSetException
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/java-client-nsr.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		client.dwcaGetDataSet("nsr", zos);
	}

}
