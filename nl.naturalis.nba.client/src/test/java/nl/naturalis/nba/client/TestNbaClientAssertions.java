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
		// System.out.println("Find by id=WAG.1706236@BRAHMS: " +
		// JsonUtil.toPrettyJson(result01));
		int expectedHashCode = 951798267;
		assertEquals(expectedHashCode, result.toString().hashCode());
	}

	@Test
	public void test_find_ids() {

		// finds(ids) - Find multiple ids

		String[] ids = { "WAG.1706278@BRAHMS", "WAG.1706277@BRAHMS" };
		Specimen[] result = specimenClient.find(ids);
		// System.out.println("Find by ids: " +
		// JsonUtil.toPrettyJson(result02));
		int expectedHashCode = 247052944;
		assertEquals(expectedHashCode, result.toString().hashCode());

		/*
		 * Produceert:
		 *
		 * http://localhost:8080/v2/specimen/findByIds/%5B%22WAG.1706278@BRAHMS%
		 * 22,%22WAG.1706277@BRAHMS%22%5D of decoded:
		 * http://localhost:8080/v2/specimen/findByIds/["WAG.1706278@BRAHMS",
		 * "WAG.1706277@BRAHMS"]
		 * 
		 * zonder resultaten. Dit zou moeten produceren:
		 * 
		 * http://localhost:8080/v2/specimen/findByIds/WAG.1706278@BRAHMS,WAG.
		 * 1706277@BRAHMS
		 * 
		 * Met 2 resultaten.
		 */

	}

	@Test
	public void test_findByUnitID() {

		// findByUnitID(unitID) - test specimen with unitID = "WAG.1706236"

		String unitID = "WAG.1706236";
		Specimen[] result = specimenClient.findByUnitID(unitID);
		// System.out.println("Find by UnitID=WAG.1706236: " +
		// JsonUtil.toPrettyJson(result));
		int expectedHasCode = 1497510745;
		assertEquals(expectedHasCode, result.toString().hashCode());
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
		
		QuerySpec querySpec = new QuerySpec();
		String country = "gatheringEvent.country";
		QueryCondition condition = new QueryCondition(country, EQUALS, "Netherlands");
		querySpec.addCondition(condition);
		
		String family = "identifications.defaultClassification.family";
		condition = new QueryCondition(family, EQUALS, "Plantaginaceae");
		querySpec.addCondition(condition);

		family = "identifications.defaultClassification.family";
		condition = new QueryCondition(family, EQUALS, "Apiaceae");
		querySpec.setLogicalOperator(OR);
		querySpec.addCondition(condition);
		
		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
		System.out.println(JsonUtil.toPrettyJson(querySpec));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));
		
//		int setSize = 10;
//		int resultSize = 192;
//		assertEquals(setSize, specimenResults.size());
//		assertEquals(resultSize, specimenResults.getTotalSize());
			
		
		
		
	}

}