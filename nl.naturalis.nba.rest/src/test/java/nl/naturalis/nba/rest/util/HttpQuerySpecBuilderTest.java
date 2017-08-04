package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.utils.ConfigObject.isTrueValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.rest.exception.HTTP400Exception;

/**
 * Unit test for HttpQuerySpecBuilder
 * 
 * @author Tom Gilissen
 */

public class HttpQuerySpecBuilderTest<qs> {

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/*
	 * Test of request containing illegal parameter "_querySpec"
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCheckParams() throws HTTP400Exception
	{
		String param1 = "_querySpec",			value1 = "whatever";
		String param2 = "sourceSystem.code",	value2 = "CRS";
		String param3 = "_fields",				value3 = "Aves";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing a duplicate parameter
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildDuplicateParam() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code",	value1 = "CRS";
		String param2 = "collectionType",		value2 = "Aves";
		String param3 = param2,					value3 = "Mammalia";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.get(param3).add(value3);

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing a parameter "querySpec"
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseParameterQuerySpec() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "querySpec", value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing sort parameters
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseParameterSortFields() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "collectionType", value2 = "Aves";
		String param3 = "_sortFields", value3 = "unitID:ASC,id:DESC,sourceSystemId:ASC,id:DESC";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		// Build a hashmap of sort parameters from value 3 ...
		MultivaluedHashMap<Path, SortOrder> fieldsExpected = new MultivaluedHashMap<>();
		fieldsExpected.add(new Path("unitID"), SortOrder.ASC);
		fieldsExpected.add(new Path("id"), SortOrder.DESC);
		fieldsExpected.add(new Path("sourceSystemId"), SortOrder.ASC);
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		
		// ... and another of the sort parameters included in the query spec
		MultivaluedHashMap<Path, SortOrder> fieldsActual = new MultivaluedHashMap<>();
		for (SortField field : qs.getSortFields()) {
			fieldsActual.add(field.getPath(), field.getSortOrder());
		}
		assertTrue("Test of sort parameters", fieldsExpected.equals(fieldsActual));

		// Test again, but now with an invalid sort order
		value3 = "unitID:ASC,id:test,id:ASC";
		parameterMap.get(param3).set(0, value3);
		new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing a parameter starting with an underscore
	 */
	@Test
	public void testBuildCaseIllegalParameter() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "_test", value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		Boolean paramTest = false;
		String msg = "";
		try {
			@SuppressWarnings("unused")
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
			msg = ex.getLocalizedMessage();
		}
		assertTrue("Test illegal parameter",
				paramTest && msg.contains("Unknown or illegal parameter"));
	}

	
	/*
	 * Test of request containing a parameter that equals @NULL@
	 */
	@Test
	public void testBuildCaseParameterEqualsNull()
	{
		String param1 = "sourceSystem.code", value1 = "BRAHMS";
		String param2 = "collectionType", value2 = "@NULL@";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		Boolean nullValueTest = false;
		for (QueryCondition condition : qs.getConditions()) {
			if (condition.getField().toString().equals(param2)) {
				if (condition.getValue() == null && condition.getOperator() == EQUALS) {
					nullValueTest = true;
					break;
				}
			}
		}
		assertTrue("Test NULL value in parameter", nullValueTest);
	}

	
	/*
	 * Test of request containing a parameter that equals @NOT_NULL@
	 */
	@Test
	public void testBuildCaseParameterEqualsNotNull()
	{
		String param1 = "sourceSystem.code", value1 = "BRAHMS";
		String param2 = "collectionType", value2 = "@NOT_NULL@";
		String param3 = "kindOfUnit", value3 = "@NULL@";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		Boolean notNullValueTest = false;
		for (QueryCondition condition : qs.getConditions()) {
			if (condition.getField().toString().equals(param2)) {
				if (condition.getValue() == null && condition.getOperator() == NOT_EQUALS) {
					notNullValueTest = true;
					break;
				}
			}
		}
		assertTrue("Test NULL value in parameter", notNullValueTest);
	}

	
	/*
	 * Test of request containing filter fields
	 */
	@Test
	public void testBuildCaseParameterFields()
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "collectionType", value2 = "Aves";
		String param3 = "_fields",
				value3 = "unitID, recordBasis,gatheringEvent.country ,identifications.defaultClassification.genus";

		List<Path> fieldsExpected = new ArrayList<>();
		String[] chunks = value3.split(",");
		for (String chunk : chunks) {
			fieldsExpected.add(new Path(chunk.trim()));
		}

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		List<Path> fieldsActual = qs.getFields();
		assertTrue("Test filter", fieldsExpected.equals(fieldsActual));
	}

	
	/*
	 * Test of _ignoreCase parameter
	 */
	@Test
	public void testBuildGetComparisonOperator()
	{
		String param1 = "sourceSystem.code", value1 = "BRAHMS";
		String param2 = "collectionType", value2 = "Botany";
		String param3 = "license", value3 = "CC0";
		String param4 = "_fields", value4 = "unitID,recordBasis,gatheringEvent.country";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));
		parameterMap.put(param4, new ArrayList<>(Arrays.asList(value4)));

		// Start by testing EQUALS_IC
		String param5 = "_ignoreCase", value5 = "true";
		parameterMap.put(param5, new ArrayList<>(Arrays.asList(value5)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		Boolean operatorTest = false;
		for (QueryCondition cond : qs.getConditions()) {
			if (cond.getOperator() == EQUALS_IC) {
				operatorTest = true;
			}
			else {
				operatorTest = false;
				break;
			}
		}
		assertTrue("Test of parameter: _ignoreCase=true", operatorTest);

		// ... next, test for EQUALS
		value5 = ""; // NULL, "" or " " should all lead to EQUALS
		parameterMap.get(param5).set(0, value5);

		uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		qs = new HttpQuerySpecBuilder(uriInfo).build();

		operatorTest = false;
		for (QueryCondition cond : qs.getConditions()) {
			if (cond.getOperator() == EQUALS) {
				operatorTest = true;
			}
			else {
				operatorTest = false;
				break;
			}
		}
		assertTrue("Test of parameter: _ignoreCase=\"\"", operatorTest);
	}

	
	/*
	 * Test of _size and _from parameters
	 */
	@Test (expected = HTTP400Exception.class)
	public void testGetIntParam() throws HTTP400Exception
	{
		// First, test with allowed values and check if the values compare.
		String param1 = "sourceSystem.code", value1 = "BRAHMS";
		String param2 = "_size", value2 = "100";
		String param3 = "_from", value3 = "100";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		assertEquals("Test of Size parameter ", value2, qs.getSize().toString());
		assertEquals("Test of From parameter ", value3, qs.getFrom().toString());

		// Next, test again but now with a disallowed value
		parameterMap.get(param2).set(0, "test");
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of the _querySpec parameter (method: buildFromSearchSpecParam)
	 */
	public void testBuildFromSearchSpecParam() throws HTTP400Exception
	{
		// Start by testing a query spec parameter together with another parameter
		String param1 = "sourceSystem.code",	value1 = "BRAHMS";
		String param2 = "_querySpec", 			value2 = "{\"conditions\":[{\"field\":\"collectionType\",\"operator\":\"=\",\"value\":\"Botany\"}]}";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		Boolean paramTest = false;
		try {
			new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;

		}
		assertTrue("Test Build with Query Spec parameter combined with another parameter", paramTest);

		// Continue by testing 2 Query Specs at once
		param1 = "_querySpec";
		value1 = "{\"conditions\":[{\"field\":\"collectionType\",\"operator\":\"=\",\"value\":\"Botany\"}]}";
		value2 = "{\"conditions\":[{\"field\":\"collectionType\",\"operator\":\"=\",\"value\":\"Aves\"}]}";

		parameterMap.clear();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1, value2)));

		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		paramTest = false;
		try {
			new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;

		}
		assertTrue("Test Build with 2 Query Specs", paramTest);

		// Next, test with one Query Spec, but an empty one
		parameterMap.clear();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList("")));

		paramTest = false;
		try {
			new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
		}
		assertTrue("Test Build with empty search spec", paramTest);

		// And finally test a Query Spec in JSON format
		paramTest = false;
		parameterMap.clear();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		
		try {
			new HttpQuerySpecBuilder(uriInfo).build();
			paramTest = true;
		}
		catch (HTTP400Exception ex) {
			// This test should NOT lead to a HTTP400Exception
		}
		assertTrue("Test Build with a query spec (JSON)", paramTest);
	}

	
	/*
	 * Final test that compares the expected query spec and the actuel query spec
	 */
	@Test
	public void testBuild()
	{

		// The parameters used in the Human Readable Query
		String param1 = "sourceSystem.code",		value1 = "CRS";
		String param2 = "collectionType",			value2 = "Hymenoptera";
		String param3 = "kindOfUnit",				value3 = "WholeOrganism";
		String param4 = "gatheringEvent.country",	value4 = "Greece";

		String param5 = "_logicalOperator",			logicalOperatorStr = "AND";
		String param6 = "_size",					sizeStr = "10";
		String param7 = "_from",					fromStr = "25";
		String param8 = "_fields",					fieldsStr = "sourceSystemId,identifications.scientificName.fullScientificName";
		String param9 = "_sortFields",				sortFieldsStr = "id,identifications.scientificName.fullScientificName:DESC";
		String param10 = "_ignoreCase",				ignoreCaseStr = "true";

		ComparisonOperator comparisonOperatorStr = EQUALS;
		if (isTrueValue(ignoreCaseStr)) {
			comparisonOperatorStr = EQUALS_IC;
		}

		// Build the Actual Query Spec
		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));
		parameterMap.put(param4, new ArrayList<>(Arrays.asList(value4)));

		parameterMap.put(param5, new ArrayList<>(Arrays.asList(logicalOperatorStr)));
		parameterMap.put(param6, new ArrayList<>(Arrays.asList(sizeStr)));
		parameterMap.put(param7, new ArrayList<>(Arrays.asList(fromStr)));
		parameterMap.put(param8, new ArrayList<>(Arrays.asList(fieldsStr)));
		parameterMap.put(param9, new ArrayList<>(Arrays.asList(sortFieldsStr)));
		parameterMap.put(param10, new ArrayList<>(Arrays.asList(ignoreCaseStr)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qsActual = new HttpQuerySpecBuilder(uriInfo).build();

		// Build the Expected Query Spec
		QuerySpec qsExpected = new QuerySpec();
		QueryCondition cond1 = new QueryCondition(param1, comparisonOperatorStr, value1);
		QueryCondition cond2 = new QueryCondition(param2, comparisonOperatorStr, value2);
		QueryCondition cond3 = new QueryCondition(param3, comparisonOperatorStr, value3);
		QueryCondition cond4 = new QueryCondition(param4, comparisonOperatorStr, value4);
		qsExpected.addCondition(cond1);
		qsExpected.addCondition(cond2);
		qsExpected.addCondition(cond3);
		qsExpected.addCondition(cond4);

		qsExpected.setLogicalOperator(LogicalOperator.parse(logicalOperatorStr));
		qsExpected.setSize(Integer.parseInt(sizeStr));
		qsExpected.setFrom(Integer.parseInt(fromStr));
		qsExpected.addFields(fieldsStr.split(","));
		List<SortField> sortFields = new ArrayList<>();
		for (String sortFieldStr : sortFieldsStr.split(",")) {
			if (sortFieldStr.indexOf(":") < 0) {
				sortFields.add(new SortField(sortFieldStr, SortOrder.ASC));
			}
			else {
				sortFields.add(new SortField(sortFieldStr.split(":")[0],
						SortOrder.parse(sortFieldStr.split(":")[1])));
			}
		}
		qsExpected.setSortFields(sortFields);

		// Verify if both Query Specs are equal
		assertTrue("Comparison of Human Readable and Complex Query Spec",
				compareQuerySpecs(qsActual, qsExpected));
	}

	
	/**
	 * Compares two Query Specs
	 * 
	 * @param qs1  QuerySpec one
	 * @param qs2  QuerySpec two
	 * @return  true if the Query Specs are equal, false otherwise
	 */
	static Boolean compareQuerySpecs(QuerySpec qs1, QuerySpec qs2)
	{

		// Compare the Logical Operator
		if (qs1.getLogicalOperator() != qs2.getLogicalOperator())
			return false;

		// Compare From and Size
		if (qs1.getSize() != qs2.getSize() || qs1.getFrom() != qs2.getFrom())
			return false;

		// Compare the Fields
		if (qs1.getFields().size() != qs2.getFields().size())
			return false;
		for (Path field : qs1.getFields()) {
			if (!qs2.getFields().contains(field)) {
				return false;
			}
		}

		// Compare the Sort Fields
		if (qs1.getSortFields().size() != qs2.getSortFields().size()) {
			return false;
		}

		Map<Path, SortOrder> sortMap1 = new HashMap<>();
		for (SortField sortField : qs1.getSortFields()) {
			sortMap1.put(sortField.getPath(), sortField.getSortOrder());
		}
		Map<Path, SortOrder> sortMap2 = new HashMap<>();
		for (SortField sortField : qs2.getSortFields()) {
			sortMap2.put(sortField.getPath(), sortField.getSortOrder());
		}
		if (!sortMap1.equals(sortMap2))
			return false;

		// Compare the Query Conditions
		Map<Path, ArrayList<String>> map1 = new HashMap<>();
		for (QueryCondition cond : qs1.getConditions()) {
			map1.put(cond.getField(), new ArrayList<>(
					Arrays.asList(cond.getOperator().toString(), cond.getValue().toString())));
		}

		Map<Path, ArrayList<String>> map2 = new HashMap<>();
		for (QueryCondition cond : qs2.getConditions()) {
			map2.put(cond.getField(), new ArrayList<>(
					Arrays.asList(cond.getOperator().toString(), cond.getValue().toString())));
		}

		if (!map1.equals(map2))
			return false;

		// Query Specs are equal
		return true;
	}
}
