package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.utils.ConfigObject.isTrueValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;


public class HttpQuerySpecBuilderTest {

	private MultivaluedHashMap<String, String> parseQueryParameters(String url) {
		String[] urlParts = url.split("\\?");
		String query = urlParts[1];
	    String[] params = query.split("&");
	    MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
	    for (String param : params)
	    {
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
	}

	@After
	public void tearDown() throws Exception
	{
	}

	
	@Test
	public void testBuild() throws URISyntaxException
	{

		// Test URL: http://localhost:8080/v2/specimen/query/?sourceSystem.code=CRS&collectionType=Aves&gatheringEvent.country=@NOT_NULL@&_fields=gatheringEvent.country";
		
		String param1 = "sourceSystem.code";
		String value1 = "CRS";
		
		String param2 = "collectionType";
		String value2 = "Aves";
		
		String param3 = "gatheringEvent.country";
		String value3 = "@NOT_NULL@";
		
		String parameters = param1 + "=" + value1 + "&" + param2 + "=" + value2 + "&" + param3 + "=" + value3;
		
		HashMap paramsExpected = new HashMap();
		paramsExpected.put(param1, value1);
		paramsExpected.put(param2, value2);
		paramsExpected.put(param3, null);
		
		String field1 = "gatheringEvent.country";
		String fields = "_fields=" + field1;
		
		String baseURI = "http://localhost:8080/v2";
		String documentType = "specimen";
		
		String requestURI = baseURI + "/" + documentType + "/query/?" + parameters + "&" + fields; 
		

		UriInfo uriInfo = mock(UriInfo.class);		
        when(uriInfo.getRequestUri()).thenReturn(new URI(requestURI)); 
        when(uriInfo.getBaseUri()).thenReturn(new URI(baseURI)); 
        when(uriInfo.getQueryParameters()).thenReturn(parseQueryParameters(baseURI + requestURI));
        
        assertEquals("Test baseURI", new URI(baseURI), uriInfo.getBaseUri());
        assertEquals("Test requestURI", new URI(requestURI), uriInfo.getRequestUri());

        QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
        
        HashMap paramsActual = new HashMap();
        for (QueryCondition condition : qs.getConditions()) {
        	System.out.println("Condition: " + condition.getField() + " = " + condition.getValue() );
        	paramsActual.put(condition.getField(), condition.getValue());
        }
        
        assertTrue(paramsExpected.equals(paramsActual));
		
        assertTrue("QS", (qs != null));

        
        
	}
	
}


