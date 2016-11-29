package nl.naturalis.nba.client;

import java.io.FileNotFoundException;

import nl.naturalis.nba.api.query.InvalidQueryException;

public class TestGetIdForLocality {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		GeoAreaClient client = session.getGeoAreaClient();
		String s = client.getIdForLocality("Dongen");
		System.out.println("Document ID for locality Dongen: " + s);
	}

}
