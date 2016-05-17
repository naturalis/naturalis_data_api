package nl.naturalis.nba.client;

import nl.naturalis.nba.api.ISpecimenAPI;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBA nba = new ClientConfigurator().setBaseUrl(baseUrl).create();
		ISpecimenAPI api = nba.getSpecimenAPI();
		boolean b = api.exists("ZMA.MAM.12345");
		System.out.println(b);
	}

}
