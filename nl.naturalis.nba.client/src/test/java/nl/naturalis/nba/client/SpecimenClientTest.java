package nl.naturalis.nba.client;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;

public class SpecimenClientTest {

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
		assertTrue(! client.exists("mock"));
		// check if space gets escaped
		assertTrue(! client.exists(" mo ck"));
	}
	

	@Ignore("Find gives a 404 error when nothing is found")
	public void testFind()
	{
		// TODO : the below gives a 404 error searching mock data, 
		// although findByUnitId does not. Is this what we want? 
		client.find("mock");
	}
	
	@Test
	public void testFindByUnitID()
	{
		Specimen [] sp = client.findByUnitID("mock");
		assertTrue(sp.getClass() == client.documentObjectArrayClass());
		sp = client.findByUnitID(" mo ck");
		assertTrue(sp.getClass() == client.documentObjectArrayClass());
	}

	@Test
	public void testGetNamedCollections()
	{
		String [] nc = client.getNamedCollections();
		assertTrue(nc.getClass() == String[].class);
	}
		
	@Test
	public void testGetIdsInCollection()
	{
		String [] ids = client.getIdsInCollection("mock");
		assertTrue(ids.getClass() == String[].class);
		ids = client.getIdsInCollection(" mo ck");
		assertTrue(ids.getClass() == String[].class);				
	}
			
	@Ignore("Does not throw the right exception") //(expected = NoSuchDataSetException.class)
	public void testDwcaQueryException() throws FileNotFoundException, IOException, NoSuchDataSetException
	{
		FileOutputStream out = new FileOutputStream(File.createTempFile("thisismy", "tempfile"));
		client.dwcaGetDataSet("mock", out);
		out.close();
	}
	
	@Ignore("Need actual data setup to run this test")
	public void testDwcaQuery() throws FileNotFoundException, IOException, NoSuchDataSetException 
	{
		// iterate over present collections and call dwca service
		String [] nc = client.dwcaGetDataSetNames();
		for (int i=0; i<=nc.length; i++) {
			FileOutputStream out = new FileOutputStream(File.createTempFile("thisismy", "tempfile"));
			String collection = nc[i];
			client.dwcaGetDataSet(collection, out);
			assertTrue(out.toString().length() > 0);
		}
	}
		
	@Test
	public void testDwcaGetDataSetNames()
	{
		String [] datasetNames = client.dwcaGetDataSetNames();
		assertTrue(datasetNames.getClass() == String[].class);
	}
	
	@Test
	public void testgroupByScientificName() throws InvalidQueryException
	{	    
	 		
		GroupByScientificNameQuerySpec qs = new GroupByScientificNameQuerySpec();
		qs.addCondition(new QueryCondition("unitID", ComparisonOperator.EQUALS, "mock"));
		QueryResult<ScientificNameGroup> res = client.groupByScientificName(new GroupByScientificNameQuerySpec());			
		for (Iterator<QueryResultItem<ScientificNameGroup>> it = res.iterator(); it.hasNext();) {
			ScientificNameGroup sng = it.next().getItem();
			assertTrue(sng.getClass() == ScientificNameGroup.class);
			List<Specimen> specimens = sng.getSpecimens();
			List<Taxon> taxa = sng.getTaxa();
			// TODO: we get null instead of an empty list when there are no specimens or taxa found,
			// is this what we want?
			if (specimens != null) {
        			for (Iterator<Specimen> sit = specimens.iterator(); sit.hasNext();) {
        				assertTrue(sit.next().getClass() == Specimen.class);
        			}
			}
			if (taxa != null) {
        			for (Iterator<Taxon> taxit = taxa.iterator(); taxit.hasNext();) {
        				assertTrue(taxit.next().getClass() == Taxon.class);
        			}
			}			
		}
		
		/*
		 * TODO: Move below to integration tests
		Below test is more of an integration test and should be moved
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
	    	*/
	}
	
	@Test
	public void testQuery() throws InvalidQueryException
	{
		QueryResult<Specimen> res = client.query(new QuerySpec());
		for (Iterator<QueryResultItem<Specimen>> it=res.iterator(); it.hasNext();) {
			assertTrue(it.next().getItem().getClass() == Specimen.class);
			
		}
	}

	
}
