package nl.naturalis.nba.client;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBA nba = new ClientConfigurator().setBaseUrl(baseUrl).create();
		ISpecimenAPI api = nba.getSpecimenAPI();
		Specimen[] specimens = api.findByUnitID("ZMA.MAM.12345");
		//ClientUtil.printTerse(specimens);
		String id = specimens[0].getId();
		System.out.println(id);
		Specimen first = api.find(id);
		ClientUtil.printTerse(first);
	}

}
