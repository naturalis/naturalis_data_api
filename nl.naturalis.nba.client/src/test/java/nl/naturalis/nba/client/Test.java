package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Specimen;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBA nba = new ClientConfigurator().setBaseUrl(baseUrl).create();
		SpecimenClient api = nba.getSpecimenAPI();
		Specimen[] specimens = api.findByUnitID("ZMA.MAM.12345");
		ClientUtil.printTerse(specimens);
	}

}
