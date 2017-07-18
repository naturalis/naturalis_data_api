/**
 * 
 */
package nl.naturalis.nba.client;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

/**
 * @author hettling
 *
 */
public class TaxonClientTest {

    	private static String baseUrl = "http://127.0.0.1:8080/v2";		
    	private static TaxonClient client;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		client = session.getTaxonClient();
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.TaxonClient#dwcaQuery(nl.naturalis.nba.api.QuerySpec, java.io.OutputStream)}.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws InvalidQueryException 
	 */
	@Ignore("Need actual data setup to perform this test")
	public void testDwcaQuery() throws FileNotFoundException, IOException, InvalidQueryException
	{
		// TODO: Error is "Missing required element: <mapping>", 
		// look into this
		FileOutputStream out = new FileOutputStream(File.createTempFile("thisismy", "tempfile"));
		System.out.println("DWCA QUERY");
		client.dwcaQuery(new QuerySpec(), out);
		out.close();
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.TaxonClient#dwcaGetDataSet(java.lang.String, java.io.OutputStream)}.
	 * @throws NoSuchDataSetException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testDwcaGetDataSet() throws NoSuchDataSetException, FileNotFoundException, IOException
	{
		String [] datasets = client.dwcaGetDataSetNames();
		for (int i = 0; i < datasets.length; i++) {
			FileOutputStream out = new FileOutputStream(File.createTempFile("thisismy", "tempfile"));
			String d = datasets[i];
			client.dwcaGetDataSet(d, out);
		}
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.TaxonClient#dwcaGetDataSetNames()}.
	 */
	@Test
	public void testDwcaGetDataSetNames()
	{
		String [] names = client.dwcaGetDataSetNames();
		assertTrue(names.getClass() == String[].class);
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.TaxonClient#groupByScientificName(nl.naturalis.nba.api.GroupByScientificNameQuerySpec)}.
	 * @throws InvalidQueryException 
	 */
	@Test
	public void testGroupByScientificName() throws InvalidQueryException
	{
		GroupByScientificNameQuerySpec qs = new GroupByScientificNameQuerySpec();
		qs.addCondition(new QueryCondition("acceptedName.fullScientificName", ComparisonOperator.EQUALS, "mock"));
		QueryResult<ScientificNameGroup> res = client.groupByScientificName(qs);			
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

	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.NbaClient#find(java.lang.String)}.
	 */
	@Ignore("API call gives a 404 when item not found")
	public void testFind()
	{
		// TODO: look into this 
		Taxon t = client.find("mock");		
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.NbaClient#findByIds(java.lang.String[])}.
	 */
	@Test
	public void testFindByIds()
	{
		String [] ids = {"m", "o", "c", "k"};
		Taxon[] t = client.findByIds(ids);
		assertTrue(t.getClass() == Taxon[].class);
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.NbaClient#query(nl.naturalis.nba.api.QuerySpec)}.
	 * @throws InvalidQueryException 
	 */
	@Test
	public void testQuery() throws InvalidQueryException
	{
		QueryResult<Taxon> qr = client.query(new QuerySpec());
		for (Iterator<QueryResultItem<Taxon>> it = qr.iterator(); it.hasNext();) {
			assertTrue(it.next().getItem().getClass() == Taxon.class);
		}
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.NbaClient#count(nl.naturalis.nba.api.QuerySpec)}.
	 * @throws InvalidQueryException 
	 */
	@Test
	public void testCount() throws InvalidQueryException
	{
		long count = client.count(new QuerySpec());
		assertTrue(count >= 0);
	}

	/**
	 * Test method for {@link nl.naturalis.nba.client.NbaClient#getDistinctValues(java.lang.String, nl.naturalis.nba.api.QuerySpec)}.
	 */
	@Ignore("Method not yet implemented in TaxonClient or NBA client")
	public void testGetDistinctValues()
	{
		fail("Not yet implemented");
	}

}
