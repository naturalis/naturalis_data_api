package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Specimen;

public class Demo {

	public static void main(String[] args)
	{

		// String baseUrl = "http://localhost:8080/v2";
		String baseUrl = "http://145.136.242.164:8080/v2";
		
		ClientConfig config = new ClientConfig();
		config.setBaseUrl(baseUrl);
		
		NbaSession session = new NbaSession(config);
		
		SpecimenClient specimenClient = session.getSpecimenClient(); 
		Specimen specimenResults[] = specimenClient.findByUnitID("WAG.1706236");
		for (Specimen specimen : specimenResults) 
		{
			ClientUtil.printTerse(specimen);
		}
		
		System.out.println("\n");
		
		// http://145.136.242.164:8080/v2/geo/getGeoJsonForLocality/Leiden
		
		
		GeoAreaClient geoClient = session.getGeoAreaClient();
		System.out.println(geoClient.getGeoJsonForLocality("Leiden").getClass());
		

		
		// Start an NBA session
//		String baseUrl = "http://localhost:8080/v2";
		//String baseUrl = "http://145.136.242.164:8080/v2";
//		NbaSession session = new NbaSession(new ClientConfig(baseUrl));

		//		ISpecimenAccess client = session.getSpecimenClient();		
		//		String field = "identifications.taxonomicEnrichments.sourceSystem.code";
		//		QueryCondition condition = new QueryCondition(field, "!=", null);
		//		QuerySpec query = new QuerySpec();
		//		query.addCondition(condition);
		//		QueryResult<?> result = client.query(query);
		//		ClientUtil.printTerse(result);

		//		IMultiMediaObjectAccess client = session.getMultiMediaObjectClient();
		//		QueryCondition condition = new QueryCondition("unitID", LIKE, "AVES.147969");
		//		QuerySpec query = new QuerySpec();
		//		query.addCondition(condition);
		//		QueryResult<?> result = client.query(query);
		//		ClientUtil.printTerse(result);

//		IScientificNameGroupAccess client = session.getNameGroupClient();
//		QueryCondition condition0 = new QueryCondition("specimenCount", ">", 10);
//		QueryCondition condition1 = new QueryCondition("specimens.gatheringEvent.gatheringPersons.fullName", "=", "Springer, LA");
//		
//		ScientificNameGroupQuerySpec query = new ScientificNameGroupQuerySpec();
//		query.setConstantScore(true);
//		query.addCondition(condition0);
		//query.addCondition(condition1);
//		QueryResult<?> result = client.querySpecial(query);
		//ClientUtil.printTerse(result);

		//		ITaxonAccess client = session.getTaxonClient();
		//		String field = "acceptedName.genusOrMonomial";
		//		QueryCondition condition = new QueryCondition(field, "!=", null);
		//		QuerySpec query = new QuerySpec();
		//		query.addCondition(condition);
		
		

	}

}
