package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
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

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.rest.exception.HTTP400Exception;

public class HttpQuerySpecBuilderTest {

	private static String baseURI;
	private static String documentType;

	private static MultivaluedHashMap<String, String> parseQueryParameters(String url)
	{
		String[] urlParts = url.split("\\?");
		String query = urlParts[1];
		String[] params = query.split("&");
		MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
		for (String param : params) {
			String[] pair = param.split("=");
			String key = pair[0];
			String value = pair[1];
			map.add(key, value);
		}
		return map;
	}

	@Before
	public void setUp() throws Exception
	{
		baseURI = "http://localhost:8080/v2";
		documentType = "specimen";
	}

	@After
	public void tearDown() throws Exception
	{
	}

	/*
	 * Test of URL request: tests whether a request containing illegal parameters will fail
	 * 
	 * http://localhost:8080/v2/specimen/query/?sourceSystem.code=CRS&
	 * collectionType=Aves&_fields=recordBasis,gatheringEvent.country
	 */
	@Test(expected = HTTP400Exception.class)
	public void testCheckParams() throws Exception
	{
		String param1 = "_querySpec";
		String value1 = "whatever";

		String param2 = "sourceSystem.code";
		String value2 = "CRS";

		String param3 = "_fields";
		String value3 = "Aves";

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
	 * Test whether a human readable URL request containing a specific parameter twice, will fail
	 * Example URL:
	 * http://localhost:8080/v2/specimen/query/?sourceSystem.code=CRS&collectionType=Aves&collectionType=Mammalia
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildDuplicateParam() throws URISyntaxException
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";

		String param2 = "collectionType";
		String[] value2 = {"Aves", "Mammalia"};

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		
		@SuppressWarnings("unused")
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of human readable URL: request containing a parameter with value
	 * "querySpec"
	 */
	@Test(expected = HTTP400Exception.class)
	public void testBuildCaseQuerySpec() throws URISyntaxException
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";

		String param2 = "querySpec";
		String value2 = "test";

		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);
		
		@SuppressWarnings("unused")
		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
	}

	/*
	 * Test of human readable URL request: parameter illegally beginning
	 * with underscore
	 */
	@Test
	public void testBuildCaseDefault() throws URISyntaxException
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";

		String param2 = "_test";
		String value2 = "test";

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
	 * Test of human readable URL request: value equals @NULL@
	 */
	@Test
	public void testBuildDefaultEqualsNull() throws URISyntaxException
	{
		String param1 = "sourceSystem.code";
		String value1 = "BRAHMS";

		String param2 = "collectionType";
		String value2 = "@NULL@";

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
				}
			}
		}
		assertTrue("Test NULL value in parameter", nullValueTest);
	}

	/*
	 * Test of human readable URL request: filter fields
	 * http://localhost:8080/v2/specimen/query/?sourceSystem.code=CRS&
	 * collectionType=Aves&_fields=recordBasis,gatheringEvent.country
	 */
	@Test
	public void test_05_Build() throws URISyntaxException
	{
		String param1 = "sourceSystem.code";
		String value1 = "CRS";

		String param2 = "collectionType";
		String value2 = "Aves";

		String param3 = "_fields";
		String value3 = "recordBasis,gatheringEvent.country";

		List<Path> value4 = new ArrayList<>();
		value4.add(new Path("recordBasis"));
		value4.add(new Path("gatheringEvent.country"));
		
		MultivaluedHashMap<String, String> parameterMap = new MultivaluedHashMap<String, String>();
		parameterMap.put(param1, new ArrayList<>(Arrays.asList(value1)));
		parameterMap.put(param2, new ArrayList<>(Arrays.asList(value2)));
		parameterMap.put(param3, new ArrayList<>(Arrays.asList(value3)));

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(parameterMap);

		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		Boolean filterTest = false;

		for (QueryCondition condition : qs.getConditions()) {
			// System.out.println("Condition in test: " + condition.getField() + " " + condition.getOperator() + " " + condition.getValue());
			if (condition.getField().toString().equals("_filter")) {
				System.out.println("Condition: " + condition.getField() + " "
						+ condition.getOperator() + " " + condition.getValue());
				if (condition.getValue() == value3) {
					filterTest = true;
				}
			}
		}
		assertTrue("Test filter", filterTest);
	}

	@Test
	public void test_099_Build() throws URISyntaxException
	{
		// Test URL: 
		// http://localhost:8080/v2/specimen/query/?sourceSystem.code=CRS&collectionType=Aves&gatheringEvent.country=@NOT_NULL@&_fields=gatheringEvent.country

		String param1 = "sourceSystem.code";
		String value1 = "CRS";

		String param2 = "collectionType";
		String value2 = "Aves";

		String param3 = "gatheringEvent.country";
		String value3 = "@NOT_NULL@";

		String param4 = "_ignoreCase";
		String value4 = "true";

		String parameters = param1 + "=" + value1 + "&" + param2 + "=" + value2 + "&" + param3 + "="
				+ value3 + "&" + param4 + "=" + value4;

		Map<String, String> paramsExpected = new HashMap<>();
		paramsExpected.put(param1, value1);
		paramsExpected.put(param2, value2);
		paramsExpected.put(param3, null);
		paramsExpected.put(param4, value4);

		String field1 = "gatheringEvent.country";
		String fields = "_fields=" + field1;

		String requestURI = baseURI + "/" + documentType + "/query/?" + parameters + "&" + fields;

		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getRequestUri()).thenReturn(new URI(requestURI));
		when(uriInfo.getBaseUri()).thenReturn(new URI(baseURI));
		when(uriInfo.getQueryParameters()).thenReturn(parseQueryParameters(baseURI + requestURI));

		assertEquals("Test baseURI", new URI(baseURI), uriInfo.getBaseUri());
		assertEquals("Test requestURI", new URI(requestURI), uriInfo.getRequestUri());

		QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();

		// Comparison operator
		// &_ignoreCase=true
		// &_ignoreCase=1
		// &_ignoreCase=false
		// &_ignoreCase=

		// ... 

		HashMap paramsActual = new HashMap();
		for (QueryCondition condition : qs.getConditions()) {
			// System.out.println("Condition: " + condition.getField() + " " + condition.getOperator() + " " + condition.getValue());
			// paramsActual.put(condition.getField(), condition.getValue());
		}

		// assertTrue(paramsExpected.equals(paramsActual));

		// assertTrue("QS", (qs != null));

		/**
		 * Map<String, String[]> paramsURL = new HashMap<>(); String[] p1 =
		 * {"CRS"}; paramsURL.put("sourceSystem.code", p1); String[] p2 =
		 * {"Aves", "Mammalia"}; paramsURL.put("collectionType", p2);
		 * 
		 * for (String key : paramsURL.keySet()) { System.out.println(key + " :
		 * " + Arrays.toString(paramsURL.get(key))); }
		 * 
		 * for (Entry<String, String[]> entry : paramsURL.entrySet()) {
		 * System.out.println(entry.getKey() + " : " +
		 * Arrays.toString(entry.getValue())); }
		 * 
		 * System.out.println("Keys: " + paramsURL.keySet());
		 * 
		 */

	}

}
