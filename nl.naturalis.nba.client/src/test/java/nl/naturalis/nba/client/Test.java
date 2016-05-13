package nl.naturalis.nba.client;

import org.domainobject.util.debug.BeanPrinter;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;

public class Test {

	public static void main(String[] args)
	{
		NBAClient client = new NBAClientBuilder().setBaseUrl("http://localhost:8080/v2").build();
		ISpecimenAPI api = client.getSpecimenAPI();
		Specimen[] specimens = api.findByUnitID("ZMA.MAM.12345");
		BeanPrinter.out(specimens);
	}

}
