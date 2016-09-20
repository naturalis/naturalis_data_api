package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.domainobject.util.IOUtil;

import nl.naturalis.nba.api.NoSuchDataSetException;

public class TestDwcaGetDataSet {

	public static void main(String[] args) throws FileNotFoundException, NoSuchDataSetException
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/java-client-nsr.zip");
		client.dwcaGetDataSet("nsr", fos);
		IOUtil.close(fos);
	}

}
