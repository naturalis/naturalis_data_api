package nl.naturalis.nba.client;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import static nl.naturalis.nba.api.LogicalOperator.OR;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;

public class TestNbaClientAssertions {

	private static String baseUrl = "http://localhost:8080/v2";
	private static NbaSession session;
	private static SpecimenClient specimenClient;

	@Before
	public void before() {
		// Initialise an NBA session
		session = new NbaSession(new ClientConfig(baseUrl));

		// Initialize client
		specimenClient = session.getSpecimenClient();
	}

	@Test
	public void test_exists_unitID() {

		// exists(unitID)
		assertTrue(specimenClient.exists("WAG.1706277"));
		assertFalse(specimenClient.exists("ABC.123456789"));
	}

	@Test
	public void test_find_id() {

		// find(id) - Find specimen with ID = "WAG.1706236@BRAHMS"
		
		String id = "WAG.1706236@BRAHMS";
		Specimen result = specimenClient.find(id);
		// System.out.println("hash: " + test.hashCode());
		// System.out.println("Find by id=WAG.1706236@BRAHMS: " + JsonUtil.toPrettyJson(result));
		int expectedHashCode = -887374660;
		assertEquals(expectedHashCode, JsonUtil.toPrettyJson(result).hashCode());
	}

	@Test
	public void test_find_ids() {

		// find(ids) - Find multiple ids

		String[] ids = { "WAG.1706278@BRAHMS", "WAG.1706277@BRAHMS" };
		Specimen[] result = specimenClient.find(ids);
		int expectedHashCode = -644252716;
		assertEquals(expectedHashCode, JsonUtil.toPrettyJson(result).hashCode());

	}

	@Test
	public void test_findByUnitID() {

		// findByUnitID(unitID) - test specimen with unitID = "WAG.1706236"

		String unitID = "WAG.1706236";
		Specimen[] result = specimenClient.findByUnitID(unitID);
		int expectedHasCode = 1544902560;
		assertEquals(expectedHasCode, JsonUtil.toPrettyJson(result).hashCode()   );
	}

	@Test
	public void test_query_test_01() throws InvalidQueryException {

		QuerySpec querySpec = new QuerySpec();
		
		String field = "collectionType";
		QueryCondition condition = new QueryCondition(field, EQUALS, "Botany");
		querySpec.addCondition(condition);

		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);

		// System.out.println(JsonUtil.toPrettyJson(querySpec));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));

		int setSize = 10;
		int resultSize = 870001;
		assertEquals(setSize, specimenResults.size());
		assertEquals(resultSize, specimenResults.getTotalSize());

		// int i = 1;
		// for (QueryResultItem<Specimen> specimen : specimenResults) {
		// System.out.println("Result #" + i);
		// System.out.println(JsonUtil.toPrettyJson(specimen));
		// i++;
		// }
	}

	@Test
	public void test_query_test_02() throws InvalidQueryException {

		QuerySpec querySpec = new QuerySpec();
		
		String country = "gatheringEvent.country";
		QueryCondition condition = new QueryCondition(country, EQUALS, "Netherlands");
		querySpec.addCondition(condition);
		
		String family = "identifications.defaultClassification.family";
		condition = new QueryCondition(family, EQUALS, "Plantaginaceae");
		querySpec.addCondition(condition);
		
		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
		// System.out.println(JsonUtil.toPrettyJson(querySpec));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));
		
		int setSize = 10;
		int resultSize = 192;
		assertEquals(setSize, specimenResults.size());
		assertEquals(resultSize, specimenResults.getTotalSize());

	}
	
	@Test
	public void test_query_test_03() throws InvalidQueryException {
		
		String country = "gatheringEvent.country";
		String family = "identifications.defaultClassification.family";
		QueryCondition condition1 = new QueryCondition(country, EQUALS, "Netherlands");
		condition1.and(family, EQUALS, "Plantaginaceae");
		
		QueryCondition condition2 = new QueryCondition(country, EQUALS, "Netherlands");
		condition2.and(family, EQUALS, "Acanthaceae");

		QuerySpec query1 = new QuerySpec();
		query1.addCondition(condition1);
		QueryResult<Specimen> specimenResults = specimenClient.query(query1);
		long result1 = specimenResults.getTotalSize();
		// System.out.println("Total size: " + specimenResults.getTotalSize());

		QuerySpec query2 = new QuerySpec();
		query2.addCondition(condition2);
		specimenResults = specimenClient.query(query2);
		long result2 = specimenResults.getTotalSize();
		// System.out.println("Total size: " + specimenResults.getTotalSize());
		
		QuerySpec query3 = new QuerySpec();
		query3.addCondition(condition1);
		query3.addCondition(condition2);
		query3.setLogicalOperator(OR);
//		query3.sortBy("unitID");
//		query3.setFrom(10);
//		query3.setSize(5);
		specimenResults = specimenClient.query(query3);
		long result3 = specimenResults.getTotalSize();
		
		assertEquals(result1 + result2, result3);
		// System.out.println("Total size: " + specimenResults.getTotalSize());
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));

		
		
//		querySpec.addCondition(condition);
//		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
//		System.out.println("Total size: " + specimenResults.getTotalSize());
//
//		
//		QuerySpec querySpec2 = new QuerySpec();
//		String family1 = "identifications.defaultClassification.family";
//		condition = new QueryCondition(family1, EQUALS, "Plantaginaceae");
//		querySpec2.addCondition(condition);
//
//		querySpec2.setLogicalOperator(OR);
//		
//		String family2 = "identifications.defaultClassification.family";
//		condition = new QueryCondition(family2, EQUALS, "Apiaceae");
//		querySpec2.addCondition(condition);
//		System.out.println(JsonUtil.toPrettyJson(querySpec));
//		
//		
//		
//		System.out.println(JsonUtil.toPrettyJson(querySpec));
//		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
//		System.out.println("Total size: " + specimenResults.getTotalSize());

		
		
//		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
//		System.out.println(JsonUtil.toPrettyJson(querySpec));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));
		
//		int setSize = 10;
//		int resultSize = 192;
//		assertEquals(setSize, specimenResults.size());
//		assertEquals(resultSize, specimenResults.getTotalSize());
			
		
		
		
	}

}