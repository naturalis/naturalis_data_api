package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.query.Operator.EQUALS;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
		ISpecimenAPI api = session.getSpecimenClient();
//		Specimen[] result = api.findByUnitID("ZMA.MAM.12345");
//		ClientUtil.printTerse(result);
		String genus = "identifications.defaultClassification.genus";
		String specificEpithet = "identifications.defaultClassification.specificEpithet";
		Condition condition = new Condition("unitID", EQUALS, "ZMA.MAM.12345");
		QuerySpec qs = new QuerySpec();
		qs.setCondition(condition);
		ClientUtil.printTerse(qs);
		try {
			Specimen[] result = api.query(qs);
			ClientUtil.printTerse(result);
		}
		catch (InvalidQueryException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}

}
