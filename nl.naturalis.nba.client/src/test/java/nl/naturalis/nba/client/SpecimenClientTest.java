package nl.naturalis.nba.client;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

public class SpecimenClientTest {

    	// private String baseUrl = "http://145.136.242.167:8081/v2";
    	private String baseUrl = "http://127.0.0.1:8080/v2";		

    	private SpecimenClient client;
    	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		client = session.getSpecimenClient();	    
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testExists()
	{
		//fail("Not yet implemented");
	}

	@Test
	public void testFind()
	{
		//fail("Not yet implemented");
	}

	@Test
	public void testFindByUnitID()
	{
		//fail("Not yet implemented");
	}

	@Test
	public void testQuery()
	{
		//fail("Not yet implemented");
	}

	@Test
	public void testgroupByScientificName() throws InvalidQueryException
	{	    
	 
		/*
		 * Test aggregation on scientific name; test also runs if there is no data
		 * as for instance on a local NBA installation		 
		 */
		
		GroupByScientificNameQuerySpec qs = new GroupByScientificNameQuerySpec();	  
	    	String testGenus = "Alcyonium";
	    	qs.addCondition(new QueryCondition("identifications.scientificName.genusOrMonomial", ComparisonOperator.EQUALS, testGenus));
	    	QueryResult<ScientificNameGroup> result = client.groupByScientificName(qs);
	    	for (Iterator<QueryResultItem<ScientificNameGroup>> it = result.iterator(); it.hasNext();) {	    		
	    		// check if we have found SNGs
	    		ScientificNameGroup sng = it.next().getItem(); 
	    		assertTrue("ScientificNameGroup object in query result", sng.getClass() == ScientificNameGroup.class);

	    		// check if our genus is in one of the identifications of the specimen 
	    		List<Specimen> specimens = it.next().getItem().getSpecimens();
	    		for (Iterator<Specimen> it2 = specimens.iterator(); it2.hasNext();) {
	    			Specimen sp = it2.next();
	    			boolean genusFound = false;
	    			for (Iterator<SpecimenIdentification> it3 = sp.getIdentifications().iterator(); it3.hasNext();) {
	    				String currentGenus = it3.next().getScientificName().getGenusOrMonomial();
	    				if (currentGenus.equals(testGenus)) {
	    					genusFound = true;
	    					break;
	    				}
	    			}	    		
	    			assertTrue("Query genus found in identifications", genusFound);
	    		}
	    	}	    	
	}
}
