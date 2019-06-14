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
import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.rest.exception.HTTP400Exception;

/**
 * Unit Test for HttpGroupByScientificNameQuerySpecBuilder
 * 
 * @author Tom Gilissen
 * 
 */

public class HttpGroupByScientificNameQuerySpecBuilderTest {

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
		String param1 = "_querySpec";
		String value1 = "whatever";
		String param2 = "sourceSystem.code";
		String value2 = "CRS";
		String param3 = "_fields";
		String value3 = "unitID";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of getComparisonOperator()
	 */
	@Test
	public void testBuildGetComparisonOperator()
	{
		String param1 = "sourceSystem.code";
		String value1 = "BRAHMS";
		String param2 = "collectionType";
		String value2 = "Botany";
		String param3 = "license";
		String value3 = "CC0";
		String param4 = "_fields";
		String value4 = "unitID,recordBasis,gatheringEvent.country";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));
		parameterMap.put(param4, new ArrayList<>(Arrays.asList(value4)));

		// Start by testing EQUALS_IC
		String param5 = "_ignoreCase", value5 = "true";
		parameterMap.put(param5, new ArrayList<>(Arrays.asList(value5)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

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

		// next, test for EQUALS
		value5 = ""; // NULL, "" or " " should all lead to EQUALS
		parameterMap.get(param5).set(0, value5);

		uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();

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
	 * Test of request containing a duplicate parameter
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildDuplicateParam() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = param2;
		String value3 = "Mammalia";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.get(param3).add(value3);

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of request containing a parameter "querySpec"
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseParameterQuerySpec() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "querySpec";
		String value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of request containing a logical operator
	 */
	@Test(expected = HTTP400Exception.class)
	public void testParamOperator() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_logicalOperator";
		String logicalOperator = "AND";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(logicalOperator)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();
		assertTrue("Test with logical operator AND",
				(qs.getLogicalOperator() == LogicalOperator.AND));

		logicalOperator = "&&";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(logicalOperator)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with logical operator &&",
				(qs.getLogicalOperator() == LogicalOperator.AND));

		logicalOperator = "OR";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(logicalOperator)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with logical operator OR",
				(qs.getLogicalOperator() == LogicalOperator.OR));

		logicalOperator = "||";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(logicalOperator)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with logical operator ||",
				(qs.getLogicalOperator() == LogicalOperator.OR));

		logicalOperator = "FAIL";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(logicalOperator)));
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with illegal logical operator", true);
	}

	/*
	 * Test of _size and _from parameters
	 */
	@Test(expected = HTTP400Exception.class)
	public void testGetIntParam() throws HTTP400Exception
	{
		// First, test with allowed values and check if the values compare.
		String param1 = "sourceSystem.code";
		String value1 = "BRAHMS";
		String param2 = "_size";
		String value2 = "100";
		String param3 = "_from";
		String value3 = "100";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();
		assertEquals("Test of Size parameter ", value2, qs.getSize().toString());
		assertEquals("Test of From parameter ", value3, qs.getFrom().toString());

		// Next, test again but now with a disallowed value
		parameterMap.get(param2).set(0, "test");
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of request containing sort parameters
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseParameterSortFields() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_sortFields";
		String value3 = "unitID:ASC,id:DESC,sourceSystemId:ASC,id:DESC";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		// Build a hashmap of sort parameters from value 3 ...
		Map<Path, SortOrder> fieldsExpected = new HashMap<>();
		fieldsExpected.put(new Path("unitID"), SortOrder.ASC);
		fieldsExpected.put(new Path("id"), SortOrder.DESC);
		fieldsExpected.put(new Path("sourceSystemId"), SortOrder.ASC);

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

		// ... and another of the sort parameters included in the query spec
		Map<Path, SortOrder> fieldsActual = new HashMap<>();
		for (SortField field : qs.getSortFields()) {
			fieldsActual.put(field.getPath(), field.getSortOrder());
		}

		assertTrue("Test of sort parameters", fieldsExpected.equals(fieldsActual));

		// Test again, but now with an invalid sort order
		value3 = "unitID:ASC,id:test,id:ASC";
		parameterMap.get(param3).set(0, value3);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of request containing filter fields
	 */
	@Test
	public void testBuildCaseParameterFields()
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_fields";
		String value3 = "unitID, recordBasis,gatheringEvent.country ,identifications.defaultClassification.genus";

		List<Path> fieldsExpected = new ArrayList<>();
		String[] chunks = value3.split(",");
		for (String chunk : chunks) {
			fieldsExpected.add(new Path(chunk.trim()));
		}

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

		List<Path> fieldsActual = qs.getFields();
		assertTrue("Test filter", fieldsExpected.equals(fieldsActual));
	}

	/*
	 * Test of request containing a group sort
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testBuildCaseParamGroupSort() throws IllegalArgumentException
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_groupSort";
		String groupSort = "";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test without defining group sort explicitly (i.e. default group sort: TOP_HIT_SCORE)",
				(qs.getGroupSort() == GroupSort.TOP_HIT_SCORE));

		groupSort = "TOP_HIT_SCORE";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with group sort TOP_HIT_SCORE", (qs.getGroupSort() == GroupSort.TOP_HIT_SCORE));

		
		groupSort = "COUNT_ASC";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with group sort COUNT_ASC", (qs.getGroupSort() == GroupSort.COUNT_ASC));

		groupSort = "COUNT_DESC";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with group sort COUNT_DESC", (qs.getGroupSort() == GroupSort.COUNT_DESC));

		groupSort = "NAME_ASC";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with group sort NAME_ASC", (qs.getGroupSort() == GroupSort.NAME_ASC));

		groupSort = "NAME_DESC";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with group sort NAME_DESC", (qs.getGroupSort() == GroupSort.NAME_DESC));

		groupSort = "FAIL";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupSort)));
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with illegal logical operator", false);
	}

	@Test
	public void testBuildCaseParamGroupFilter()
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_groupFilter";
		String groupFilter = "";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupFilter)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with empty value for groupFilter", true);

		groupFilter = ".*";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupFilter)));
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with a single value as groupFilter", true);

		groupFilter = "larus fuscus,larus ridibundus";
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(groupFilter)));
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		//		for (String field : qs.getGroupFilter().getAcceptValues()) {
		//			System.out.println(field);
		//		}
		//		System.out.println(JsonUtil.toPrettyJson(qs));
		assertTrue("Test with a multiple values as groupFilter", true);
	}

	@Test
	public void testBuildCaseSpecimenNoTaxa()
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Aves";
		String param3 = "_noTaxa";
		String noTaxa = null;

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(noTaxa)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with _noTaxa = null", true);

		noTaxa = "true"; //Possible values are: "true", "1", "yes", "on", "ok"
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with _noTaxa = true", true);

		noTaxa = "false"; // Or whatever other value ...
		new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
		assertTrue("Test with _noTaxa = false", true);
	}

	/*
	 * Test of request containing a parameter starting with an underscore
	 */
	@Test
	public void testBuildCaseIllegalParameter() throws HTTP400Exception
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "_test";
		String value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		Boolean paramTest = false;
		String msg = "";
		try {
			@SuppressWarnings("unused")
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(
					uriInfo).build();
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
		String param1 = "sourceSystem.code";
		String value1 = "BRAHMS";
		String param2 = "collectionType";
		String value2 = "@NULL@";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

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
		String param1 = "sourceSystem.code";
		String value1 = "BRAHMS";
		String param2 = "collectionType";
		String value2 = "@NOT_NULL@";
		String param3 = "kindOfUnit";
		String value3 = "@NULL@";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
				.build();

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

	
	@Test
	public void testBuild()
	{
		// The parameters used in the Human Readable Query
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		String param2 = "collectionType";
		String value2 = "Hymenoptera";
		String param3 = "kindOfUnit";
		String value3 = "WholeOrganism";
		String param4 = "gatheringEvent.country";
		String value4 = "Greece";
		
		String param5 = "_logicalOperator";
		String logicalOperatorStr = "AND";

		String param6 = "_size";
		String sizeStr = "10";
		
		String param7 = "_from";
		String fromStr = "25";
		
		String param8 = "_fields";
		String fieldsStr = "sourceSystemId,identifications.scientificName.fullScientificName";
		
		String param9 = "_sortFields";
		String sortFieldsStr = "id,identifications.scientificName.fullScientificName:DESC";
		
		String param10 = "_ignoreCase";
		String ignoreCaseStr = "true";
		
		String param11 = "_groupSort";
		String groupSortStr = "NAME_DESC";
		
		String param12 = "_groupFilter";
		String groupFilterStr = "larus.*";
		
		String param13 = "_noTaxa";
		String noTaxa = "yes";

		ComparisonOperator comparisonOperatorStr = EQUALS;
		if (isTrueValue(ignoreCaseStr)) {
			comparisonOperatorStr = EQUALS_IC;
		}

		// Build the Actual Query Spec
		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<>();
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

		// Extra conditions to a regular query spec
		parameterMap.put(param11, new ArrayList<>(Arrays.asList(groupSortStr)));
		parameterMap.put(param12, new ArrayList<>(Arrays.asList(groupFilterStr)));
		parameterMap.put(param13, new ArrayList<>(Arrays.asList(noTaxa)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		GroupByScientificNameQuerySpec qsActual = new HttpGroupByScientificNameQuerySpecBuilder(
				uriInfo).build();

		// Build the Expected Query Spec
		GroupByScientificNameQuerySpec qsExpected = new GroupByScientificNameQuerySpec();
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

		qsExpected.setGroupSort(GroupSort.parse(groupSortStr));

		Filter groupFilter = new Filter();
		groupFilter.acceptRegexp(groupFilterStr);
		qsExpected.setGroupFilter(groupFilter);
		qsExpected.setNoTaxa(true);

		// Verify if both Query Specs are equal
		assertTrue("Comparison of Human Readable and Complex Query Spec #1",
				HttpQuerySpecUtil.compareQuerySpecs(qsActual, qsExpected));

		// Re-test but now with a groupFilter of more than 1 value
		groupFilterStr = "larus fuscus,larus ridibundus";

		// Rebuild the actual qs
		parameterMap.put(param12, new ArrayList<>(Arrays.asList(groupFilterStr)));
		qsActual = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();

		// and the expected qs
		groupFilter.acceptRegexp(null);
		qsExpected.setGroupFilter(groupFilter);
		groupFilter.acceptValues(groupFilterStr.split(","));
		qsExpected.setGroupFilter(groupFilter);

		// Verify if both Query Specs are equal
		assertTrue("Comparison of Human Readable and Complex Query Spec #2",
				HttpQuerySpecUtil.compareQuerySpecs(qsActual, qsExpected));

	}

}
