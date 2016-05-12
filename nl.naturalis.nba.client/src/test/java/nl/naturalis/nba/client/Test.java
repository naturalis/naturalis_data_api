package nl.naturalis.nba.client;

import nl.naturalis.nba.api.ISpecimenAPI;

public class Test {

	public static void main(String[] args)
	{
		NBAClient client = new NBAClientBuilder().setBaseUrl("http://localhost:8080/v2").build();
		ISpecimenAPI specimenAPI = client.getSpecimenAPI();
	}

}
