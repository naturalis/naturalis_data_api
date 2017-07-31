package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.UriInfo;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.rest.exception.HTTP400Exception;

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
	public void testBuildCheckParams() throws Exception
	{
		String param1 = "_querySpec", value1 = "whatever";
		String param2 = "sourceSystem.code", value2 = "CRS";
		String param3 = "_fields", value3 = "Aves";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		@SuppressWarnings("unused")
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing a duplicate parameter
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildDuplicateParam() throws URISyntaxException
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "collectionType", value2 = "Aves";
		String param3 = param2, value3 = "Mammalia";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.get(param3).add(value3);

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		
		@SuppressWarnings("unused")
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of request containing a parameter "querySpec"
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseParameterQuerySpec() throws URISyntaxException
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "querySpec", value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		
		@SuppressWarnings("unused")
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
	}

	
	/*
	 * Test of request containing sort parameters
	 */
	@Test
	public void testBuildCaseParameterSortFields() throws Exception
	{
		String param1 = "sourceSystem.code", value1 = "CRS";
		String param2 = "collectionType", value2 = "Aves";
		String param3 = "_sortFields", value3 = "unitID:ASC,id:DESC,id:ASC";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		Boolean paramCheck = true;
		MultivaluedHashMap<Path, SortOrder> fieldsExpected = new MultivaluedHashMap<>();
		String[] chunks = value3.split(",");
		for (String chunk : chunks) {
			String[] parts = chunk.split(":");
			SortOrder so = null;
			if (parts.length == 1)
			{
				fieldsExpected.add(new Path(parts[0]), SortOrder.ASC);
			}
			else if (parts.length == 2) {
				if (parts[1].toLowerCase().equals("asc"))
				{
					so = SortOrder.ASC;
				}
				else if (parts[1].toLowerCase().equals("desc"))
				{
					so = SortOrder.DESC;
				}
				else {
					paramCheck = false;
				}
				fieldsExpected.add(new Path(parts[0]), so);
			}
		}
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		
		MultivaluedHashMap<Path, SortOrder> fieldsActual = new MultivaluedHashMap<>();
		for (SortField field : qs.getSortFields()) {
			fieldsActual.add(field.getPath(), field.getSortOrder());
		}

		assertTrue("Test filter", paramCheck && fieldsExpected.equals(fieldsActual));
		
		
		// Test again, but now with an invalid sort order
		value3 = "unitID:ASC,id:test,id:ASC";
		parameterMap.get(param3).set(0, value3);
		Boolean paramTest = false;
		
		try {
			qs = new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
		}
		assertTrue("Test illegal parameter", paramTest);
	}

	
	
	/*
	 * Test of request containing a parameter starting with an underscore
	 */
	@Test
	public void testBuildCaseIllegalParameter() throws URISyntaxException
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
	public void testBuildCaseParameterEqualsNull() throws URISyntaxException
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
	public void testBuildCaseParameterEqualsNotNull() throws URISyntaxException
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
		String param3 = "_fields", value3 = "unitID, recordBasis,gatheringEvent.country ,identifications.defaultClassification.genus";
		
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
			} else {
				operatorTest = false;
				break;
			}
		}
		assertTrue("Test of parameter: _ignoreCase=true", operatorTest);
		
		// next, test for EQUALS
		value5 = ""; // NULL, "" or " " should all lead to EQUALS
		parameterMap.get(param5).set(0, value5);
		
		uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		qs = new HttpQuerySpecBuilder(uriInfo).build();
		
		operatorTest = false;
		for (QueryCondition cond : qs.getConditions()) {
			if (cond.getOperator() == EQUALS) {
				operatorTest = true;
			} else {
				operatorTest = false;
				break;
			}
		}
		assertTrue("Test of parameter: _ignoreCase=\"\"", operatorTest);		
	}
	
	/*
	 * Test of _size and _from parameters
	 */
	@Test
	public void testGetIntParam()
	{
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

		parameterMap.get(param2).set(0, "test");
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		Boolean paramTest = false;
		try {
			@SuppressWarnings("unused")
			QuerySpec qs2 = new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
			
		}
		assertTrue("Test for illegal size or from parameter", paramTest);	
	}

	/*
	 * Test of the _querySpec parameter (method: buildFromSearchSpecParam)
	 */
	@Test
	public void testBuildFromSearchSpecParam()
	{
		// Start by testing 2 Query Specs at once
		String param1 = "_querySpec";
		String value1 = "{\"conditions\":[{\"field\":\"collectionType\",\"operator\":\"=\",\"value\":\"Botany\"}]}";
		String value2 = "{\"conditions\":[{\"field\":\"collectionType\",\"operator\":\"=\",\"value\":\"Aves\"}]}";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1, value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		Boolean paramTest = false;
		try {
			@SuppressWarnings("unused")
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
			
		}
		assertTrue("Test Build with 2 Query Specs", paramTest);
		
		// Continue with an empty Query Spec
		parameterMap.remove(param1);
		parameterMap.put(param1, new ArrayList<>(Arrays.asList("")));
		
		paramTest = false;
		try {
			@SuppressWarnings("unused")
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
		}
		catch (HTTP400Exception ex) {
			paramTest = true;
		}
		assertTrue("Test Build with empty search spec", paramTest);
		
		// And conclude with a Query Spec in JSON
		paramTest = false;
		parameterMap.get(param1).remove(param1);
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		try {
			@SuppressWarnings("unused")
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			paramTest = true;
		}
		catch (Exception ex) {
			paramTest = false;
		}
		assertTrue("Test build with complex search failed", paramTest);
		// Note: it is allowed to combine a human readable AND a complex query
		// BUT: only the complex query will be run!
	}
	
	/*
	 * Final test that compares the expected query spec and the actuel query spec
	 */
	@Test
	public void testBuild()
	{
		/*
		 * Sample query
		 * 
		 * Human readable:
		 * 
		 * sourceSystem.code=CRS
		 * collectionType=Hymenoptera
		 * kindOfUnit=WholeOrganism
		 * gatheringEvent.country=Greece
		 * _ignoreCase=true
		 * _size=10
		 * _from=25
		 * _fields=sourceSystemId,identifications.scientificName.fullScientificName 
		 * _sortFields=id,identifications.scientificName.fullScientificName:DESC
		 * 
		 * 
		 * Complex:
		 * 

		 * {
		 *	   "conditions" : [
		 *       { "field" : "sourceSystem.code", "operator" : "=", "value" : "CRS" },
		 *         { "field" : "collectionType", "operator" : "EQUALS_IC", "value" : "Hymenoptera" },
		 *         { "field" : "kindOfUnit", "operator" : "EQUALS_IC", "value" : "WholeOrganism" },
		 *         { "field" : "gatheringEvent.country", "operator" : "EQUALS_IC", "value" : "Greece" } 
		 *     ],
		 *     "logicalOperator" : "AND",
		 *     "from" : 25,
		 *     "size" : 10,
		 *     "fields" : ["sourceSystemId","identifications.scientificName.fullScientificName"],
		 *       "sortFields" : [ 
		 *       { "path" : "id", "sortOrder" : "ASC" }, 
		 *       { "path" : "identifications.scientificName.fullScientificName", "sortOrder" : "DESC" } ]
		 * }
		 *
		 */
		
		String param1 = "sourceSystem.code",		value1 = "CRS";
		String param2 = "collectionType",			value2 = "Hymenoptera";
		String param3 = "kindOfUnit",				value3 = "WholeOrganism";
		String param4 = "gatheringEvent.country",	value4 = "Greece";
		String param5 = "_ignoreCase",				value5 = "true";
		String param6 = "_size",					value6 = "10";
		String param7 = "_from",					value7 = "25";
		String param8 = "_fields",					value8 = "sourceSystemId,identifications.scientificName.fullScientificName"; 
		String param9 = "_sortFields", 				value9 = "id,identifications.scientificName.fullScientificName:DESC";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));
		parameterMap.put(param4, new ArrayList<>(Arrays.asList(value4)));
		parameterMap.put(param5, new ArrayList<>(Arrays.asList(value5)));
		parameterMap.put(param5, new ArrayList<>(Arrays.asList(value6)));
		parameterMap.put(param7, new ArrayList<>(Arrays.asList(value7)));
		parameterMap.put(param8, new ArrayList<>(Arrays.asList(value8)));
		parameterMap.put(param9, new ArrayList<>(Arrays.asList(value9)));
		
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		@SuppressWarnings("unused")
		QuerySpec qsActual = new HttpQuerySpecBuilder(uriInfo).build();
		
		QuerySpec qsExpected = new QuerySpec();
		QueryCondition cond1 = new QueryCondition("sourceSystem", EQUALS_IC, "CRS");
		QueryCondition cond2 = new QueryCondition("collectionType", EQUALS_IC, "Hymenoptera");
		QueryCondition cond3 = new QueryCondition("kindOfUnit", EQUALS_IC, "WholeOrganism");
		QueryCondition cond4 = new QueryCondition("gatheringEvent.country", EQUALS_IC, "Greece");
		qsExpected.addCondition(cond1);
		qsExpected.addCondition(cond2);
		qsExpected.addCondition(cond3);
		qsExpected.addCondition(cond4);
		String[] fields = {"ignoreCase", "size","from", "fields", "sortFields"};
		qsExpected.addFields(fields);
		

	}
}
