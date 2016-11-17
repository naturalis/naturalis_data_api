package nl.naturalis.nba.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.utils.IOUtil;

public class TestDwcaGetDataSet {

	public static void main(String[] args) throws FileNotFoundException, NoSuchDataSetException
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/java-client-nsr.zip");
		client.dwcaGetDataSet("nsr", fos);
		IOUtil.close(fos);
	}

}
