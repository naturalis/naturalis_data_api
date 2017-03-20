package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.LogicalOperator.OR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.IOUtil;

public class TestNbaClientAssertions {

	// private static String baseUrl = "http://localhost:8080/v2";
	private static String baseUrl = "http://145.136.242.164:8080/v2";
	private static NbaSession session;
	private static SpecimenClient specimenClient;

	@Before
	public void before() {

		// Initialise an NBA session
		session = new NbaSession(new ClientConfig(baseUrl));

		// Initialize client
		specimenClient = session.getSpecimenClient();

	}

	/*
	 * count(QuerySpec querySpec) Returns the number of documents conforming to
	 * the provided query specification.
	 */
	@Test
	public void test_count() throws InvalidQueryException {

		QuerySpec querySpec = new QuerySpec();
		String field = "collectionType";
		QueryCondition condition = new QueryCondition(field, EQUALS, "Botany");
		querySpec.addCondition(condition);

		long resultActual = specimenClient.count(querySpec);
		// System.out.println("Count: " + specimenClient.count(querySpec));

		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
		long resultExpected = specimenResults.getTotalSize();
		// System.out.println("Expected: " + resultExpected);

		assertEquals(resultExpected, resultActual);

	}

	
	/*
	 * delete(String id, boolean immediate) Deletes the specimen with the
	 * specified system ID (as can be retrieved using Specimen.getId()).
	 */
	@Test
	public void test_delete() {

		// ...

	}

	/*
	 * dwcaGetDataSet(String name, ZipOutputStream out) Writes a DarwinCore
	 * Archive with specimens from a predefined data set to the specified output
	 * stream.
	 */
	@Test
	public void test_dwcaGetDataSet() {

		// ...
		// Afhankelijk van dwcaGetDataSetNames() (nog niet geimplementeerd)
		System.err.println("test_dwcaGetDataSet() is nog niet geimplementeerd.\n");

	}

	/*
	 * dwcaGetDataSetNames() Returns the names of all predefined data sets with
	 * specimen/occurrence data.
	 */
	@Test
	public void test_dwcaGetDataSetNames() {

		// Nog niet geimplementeerd
		System.err.println("dwcaGetDataSetNames() is nog niet geimplementeerd.\n");
		// System.out.println("DataSetNames: " +
		// specimenClient.dwcaGetDataSetNames());

	}

	
	/*
	 * dwcaQuery(QuerySpec querySpec, ZipOutputStream out) Writes a DarwinCore
	 * Archive with specimens satisfying the specified query specification to
	 * the specified output stream.
	 */
	@Test
	public void test_dwcaQuery() throws IOException, InvalidQueryException {

		FileOutputStream fos = new FileOutputStream("/home/tom/tmp/dwca.zip");
		ZipOutputStream out = new ZipOutputStream(fos);
		
		QuerySpec querySpec = new QuerySpec();
		QueryCondition condition = new QueryCondition();
		condition = new QueryCondition("identifications.defaultClassification.genus", EQUALS_IC, "tulipa");
		querySpec.addCondition(condition);
		
		specimenClient.dwcaQuery(querySpec, out);
		IOUtil.close(fos);
		
//		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
//		System.out.println("DWCA query result: " + JsonUtil.toPrettyJson(specimenResults));

		// Error:
		// Exception in thread "main" nl.naturalis.nba.client.NoSuchServiceException: 
		// The client specified a non-existent NBA service endpoint. This is a bug.

	}

	
	/*
	 * exists(String unitID)
	 * Returns whether or not the specified string is a valid UnitID (i.e. is the UnitID 
	 * of at least one specimen record).
	 */
	@Test
	public void test_exists_unitID() {

		// exists(unitID)
		assertTrue(specimenClient.exists("WAG.1706277"));
		assertFalse(specimenClient.exists("ABC.123456789"));
	}

	
	/*
	 * 	find(String id)
	 * Returns the data model object with the specified system ID, or null if there is no 
	 * data model object with the specified system ID.
	 */
	@Test
	public void test_find_id() {

		// find(id) - Find specimen with ID = "WAG.1706236@BRAHMS"

		String id = "WAG.1706236@BRAHMS";
		Specimen result = specimenClient.find(id);
		// System.out.println("hash: " + result.hashCode());
		// System.out.println("Find by id=WAG.1706236@BRAHMS: " + JsonUtil.toPrettyJson(result));
		int expectedHashCode = 1272883899;
		assertEquals(expectedHashCode, result.hashCode());
	}

	/*
	 * find(String[] ids)
	 * Returns the data model objects with the specified system IDs, or a zero-length 
	 * array no specimens were found.
	 */
	@Test
	public void test_find_ids() {

		String[] ids = { "WAG.1706278@BRAHMS", "WAG.1706277@BRAHMS" };
		Specimen[] result = specimenClient.find(ids);
		// System.out.println("Hash code: " + result.hashCode());
		int expectedHashCode = 2106592975;
		assertEquals(expectedHashCode, result.hashCode());

	}

	
	/*
	 * findByUnitID(String unitID)
	 * Retrieves a Specimen by its UnitID.
	 */
	@Test
	public void test_findByUnitID() {

		String unitID = "WAG.1706236";
		Specimen[] result = specimenClient.findByUnitID(unitID);
		// System.out.println("Hash code: " + result.hashCode());
		int expectedHasCode = 987249254;
		assertEquals(expectedHasCode, result.hashCode());
		
	}

	
	/*
	 * getDistinctValues(String forField, QuerySpec spec)
	 * Returns the unique values of the specified field.
	 */
	@Test
	public void test_getDistinctValues() throws InvalidQueryException {

		// Not yet implemented!
		
		QuerySpec querySpec = new QuerySpec();
		QueryCondition condition = new QueryCondition();
		condition = new QueryCondition("identifications.defaultClassification.genus", EQUALS_IC, "tulipa");
		querySpec.addCondition(condition);
	
		// QueryResult<Specimen> specimenResults = specimenClient.query(querySpec);
		// System.out.println("Get Distinct: %n" + JsonUtil.toPrettyJson(specimenResults));
		
		Map uniqueValues = specimenClient.getDistinctValues("identifications.defaultClassification.specificEpithet", querySpec);
		System.out.println(uniqueValues.values().toString());
	
	}

	
	/*
	 * getDistinctValuesPerGroup(String groupField, String valuesField, QueryCondition... conditions)
	 * Returns the unique values of the specified field, grouping them using another field .
	 */
	@Test
	public void test_getDistinctValuesPerGroup(){

		//..

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

		QuerySpec querySpec01 = new QuerySpec();

		String country = "gatheringEvent.country";
		String family = "identifications.defaultClassification.family";

		QueryCondition condition1 = new QueryCondition(country, EQUALS, "Netherlands");
		condition1.and(family, EQUALS, "Plantaginaceae");

		querySpec01.addCondition(condition1);
		QueryResult<Specimen> specimenResults = specimenClient.query(querySpec01);
		long result01 = specimenResults.getTotalSize();
		// System.out.println("Total size: " + specimenResults.getTotalSize());

		QuerySpec querySpec02 = new QuerySpec();

		QueryCondition condition2 = new QueryCondition(country, EQUALS, "Netherlands");
		condition2.and(family, EQUALS, "Acanthaceae");

		querySpec02.addCondition(condition2);
		specimenResults = specimenClient.query(querySpec02);
		long result02 = specimenResults.getTotalSize();
		// System.out.println("Total size: " + specimenResults.getTotalSize());

		QuerySpec querySpec03 = new QuerySpec();

		querySpec03.addCondition(condition1);
		querySpec03.addCondition(condition2);
		querySpec03.setLogicalOperator(OR);

		// Beschikbaar opties:
		// query3.sortBy("unitID");
		// query3.setFrom(10);
		// query3.setSize(5);
		specimenResults = specimenClient.query(querySpec03);
		long result03 = specimenResults.getTotalSize();

		assertEquals(result01 + result02, result03);
		// System.out.println("Total size: " + specimenResults.getTotalSize());
		// System.out.println(JsonUtil.toPrettyJson(querySpec03));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));

		// Probeer het eerste en het laatste record op te halen

		// Eerste record
		QueryResultItem<Specimen> firstRecord = specimenResults.get(0);
		// System.out.println(JsonUtil.toPrettyJson(firstRecord));

		// Laatste record of the set
		int lastRecordNumber = (int) specimenResults.getTotalSize();
		lastRecordNumber--;
		// System.out.println("Last record: " + lastRecordNumber);
		QueryResultItem<Specimen> lastRecord = specimenResults.get(9);
		// System.out.println(JsonUtil.toPrettyJson(lastRecord));

		// TODO: get the last record of the search result!!!

	}

	@Test
	public void test_get_named_collections() throws InvalidQueryException {

		String[] definedCollections = { "Living Dinos", "Strange Plants" };
		String[] collectionNames = specimenClient.getNamedCollections();
		Arrays.sort(collectionNames);
		// System.out.println("Result: " + Arrays.toString(collectionNames));
		assertTrue(Arrays.equals(collectionNames, definedCollections));

	}

	@Test
	public void test_get_ids_in_named_collection() throws InvalidQueryException {

		String result[] = specimenClient.getIdsInCollection("Strange Plants");
		// Is dit een bug???

		// System.out.println("IDs in named collection: " +
		// Arrays.toString(result));
		assertNotNull(Arrays.toString(result));
	}

	@Test
	public void test_get_distinct_values() {

		// NB: nog niet geimplementeerd in de client!!!

		// http://localhost:8080/v2/specimen/getDistinctValues/sourceSystem.code
		// Result: "BRAHMS": 870001

		// System.out.println("Distinct: " +
		// specimenClient.getDistinctValues("sourceSystem.code", null));

		// QuerySpec querySpec = new QuerySpec();
		// String field = "sourceSystem.code";
		// QueryCondition condition = new QueryCondition(field, EQUALS,
		// "BRAHMS");
		// QueryCondition condition = new QueryCondition(field, NOT_EQUALS,
		// null);
		// querySpec.addCondition(condition);
		// QueryResult<Specimen> specimenResults =
		// specimenClient.query(querySpec);
		//
		// System.out.println(JsonUtil.toPrettyJson(querySpec));
		// System.out.println(JsonUtil.toPrettyJson(specimenResults));

	}

	@Test
	public void test_save() {
		String id = "WAG.1706236@BRAHMS";
		Specimen existingRecord = specimenClient.find(id);
		// System.out.println("hash: " + test.hashCode());
		// System.out.println("Find by id=WAG.1706236@BRAHMS: " +
		// JsonUtil.toPrettyJson(existingRecord));

		Specimen testSpecimen = new Specimen();
		testSpecimen = existingRecord;
		// System.out.println("existing: " + existingRecord.toString());
		// System.out.println("test: " + testSpecimen.toString());

	}

}