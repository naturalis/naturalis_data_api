package nl.naturalis.nba.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.IScientificNameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummaryTaxon;

public class ScientificNameGroupClientTest {

    // define global variables used by all tests
    // private String baseUrl = "http://localhost:8080/v2";
    private String baseUrl = "http://145.136.242.167:8080/v2";
    private NbaSession session;
    private IScientificNameGroupAccess client;

    @Before
    public void setUp()
    {
	session = new NbaSession(new ClientConfig(baseUrl));
	client = session.getNameGroupClient();

	// Only run test if we can successfully retrieve SNG documents
	long ndocs;
	try {
	    ndocs = client.query(new ScientificNameGroupQuerySpec()).getTotalSize();
	    assumeTrue(ndocs > 0);
	} catch (InvalidQueryException e) {
	    assumeNoException(e);
	}
    }

    @Test
    public void testQuery() throws InvalidQueryException
    {
	/*
	 * Basic test of query function
	 */
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	QueryResult<ScientificNameGroup> result = client.query(qs);
	// test return type
	assertEquals(result.getClass(), QueryResult.class);
	// we should find some sng's
	assertTrue(result.getTotalSize() > 0);
    }

    @Test
    public void testQuerySpecial() throws InvalidQueryException
    {
	/*
	 * Basic test of querySpecial function
	 */
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	QueryResult<ScientificNameGroup> result = client.querySpecial(qs);
	// test return type
	assertEquals(result.getClass(), QueryResult.class);
	// we should find some sng's
	assertTrue(result.getTotalSize() > 0);
	// test return type if no results
	qs.addCondition(new QueryCondition("specimenCount", ComparisonOperator.EQUALS, Integer.MAX_VALUE));
	result = client.querySpecial(qs);
	assertNotNull(result);
	assertEquals(result.getClass(), QueryResult.class);
	assertEquals(result.getTotalSize(), 0);
    }

    @Test
    public void testQueryVsquerySpecial() throws InvalidQueryException
    {
	/*
	 * End point querySpecial was implemented because when e.g. filtering on
	 * specimens.sex=male in a query, we can still have female specimens in
	 * a name group, as long as one specimen of the name group is male. With
	 * querySpecial, another post-filtering takes place to get all males.
	 * This is documented in IScientificNameGroupAccess. Here we test
	 * exactly this example
	 */
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	qs.addCondition(new QueryCondition("specimens.sex", ComparisonOperator.EQUALS, "male"));
	QueryResult<ScientificNameGroup> resultNonspecial = client.query(qs);

	// loop over all specimens and check if we have a female specimen
	boolean allMale = true;
	for (Iterator<QueryResultItem<ScientificNameGroup>> it1 = resultNonspecial.iterator(); it1.hasNext();) {
	    for (Iterator<SummarySpecimen> it2 = it1.next().getItem().getSpecimens().iterator(); it2.hasNext();) {
		SummarySpecimen sp = it2.next();
		allMale = allMale && sp.getSex().equals(Sex.MALE);
	    }
	}
	// we should find male and female specimens
	assertTrue(!allMale);

	// same query as above but with QuerySpecial
	QueryResult<ScientificNameGroup> resultSpecial = client.querySpecial(qs);
	allMale = true;
	for (Iterator<QueryResultItem<ScientificNameGroup>> it1 = resultSpecial.iterator(); it1.hasNext();) {
	    for (Iterator<SummarySpecimen> it2 = it1.next().getItem().getSpecimens().iterator(); it2.hasNext();) {
		SummarySpecimen sp = it2.next();
		allMale = allMale && sp.getSex().equals(Sex.MALE);
	    }
	}
	// now we should find only male specimens
	assertTrue(allMale);
    }
        
    @Ignore
    public void testSummarySpecimenInNameGroup() throws InvalidQueryException
    {
	/*
	 * A ScientificNameGroup document can have associated specimens,
	 * represented by a thinner version of a Specimen (SummarySpecimen)
	 * document nested under the ScientificNameGroup. Here we test (for some
	 * of them) on real data, if all fields of the Specimen in the
	 * ScientificNameGroup match the corresponding fields in the respective
	 * Specimen document
	 * TODO: Ignored because behavior is not as expected!
	 */
	SpecimenClient sc = session.getSpecimenClient();
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();

	// only select SNGs with one or more specimen
	qs.addCondition(new QueryCondition("specimenCount", ComparisonOperator.GT, 0));
	// System.out.println(JsonUtil.toJson(qs));
	qs.setSize(1000); // caution magic number
	QueryResult<ScientificNameGroup> result = client.query(qs);
	for (Iterator<QueryResultItem<ScientificNameGroup>> it1 = result.iterator(); it1.hasNext();) {
	    // extract summary specimens
	    for (Iterator<SummarySpecimen> it2 = it1.next().getItem().getSpecimens().iterator(); it2.hasNext();) {
		SummarySpecimen sSpecimen = it2.next();
		String unitID = sSpecimen.getUnitID();
		// query for corresponding specimen
		Specimen[] spresult = sc.findByUnitID(unitID);
		// there should be only one specimen
		assertEquals("Exactly one specimen found for unitID", spresult.length, 1);
		Specimen specimen = spresult[0];

		// compare values of SpecimenSummary to Specimen object
		assertTrue("Fields in Specimen object and SummarySpecimen object match",
			sSpecimen.isSummaryOf(specimen));
	    }
	}
    }
    
    @Ignore
    public void testSummaryTaxaInNameGroup() throws InvalidQueryException {
	/*
	 * A ScientificNameGroup document can have associated taxa,
	 * represented by a thinner version of a Taxon (SummaryTaxon)
	 * document nested under the ScientificNameGroup. Here we test (for some
	 * of them) on real data, if all fields of the Taxon in the
	 * ScientificNameGroup match the corresponding fields in the respective
	 * Taxon document
	 * TODO: Ignored because behavior is not as expected!
	 */
	TaxonClient tc = session.getTaxonClient();
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	// only select SNGs with one or more specimen
	qs.addCondition(new QueryCondition("taxonCount", ComparisonOperator.GT, 0));
	qs.setSize(100); // caution magic number
	QueryResult<ScientificNameGroup> result = client.query(qs);
	for (Iterator<QueryResultItem<ScientificNameGroup>> it1 = result.iterator(); it1.hasNext();) {
	    // extract summary taxa
	    ScientificNameGroup sng = it1.next().getItem();
	    List<SummaryTaxon> summaryTaxa = sng.getTaxa();
	    
	    for (Iterator<SummaryTaxon> it2 = summaryTaxa.iterator(); it2.hasNext();) {
		SummaryTaxon st = it2.next();
    	    
		// test that all SummaryTaxon objects from an SNG match their Taxon counterpart 
    	    	Taxon t = tc.find(st.getId());
    	    	System.out.println(t.getId());
    	    	System.out.println(sng.getId());
    	    	assertTrue(st.isSummaryOf(t));		    	    	    	    
	    }		
	}
    }
    
    @Test
    public void testNameGroupForTaxa() throws InvalidQueryException {
	/* 
	 * Tests if every taxon has a SNG and that this SNG is 
	 * listed in the scientificNameGroup field of the taxon
	 */
	TaxonClient tc = session.getTaxonClient();

	QuerySpec tqs = new QuerySpec();
	tqs.setSize(1000); //caution magic number
	QueryResult<Taxon> allTaxa = tc.query(tqs);
	for (Iterator<QueryResultItem<Taxon>> it = allTaxa.iterator(); it.hasNext();) {
	    Taxon tax = it.next().getItem();
	    String taxonSNG = tax.getAcceptedName().getScientificNameGroup();

	    // check if we find back that scientific name group by querying for the SNG 
	    ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	    qs.addCondition(new QueryCondition("name", ComparisonOperator.EQUALS, taxonSNG));
	    QueryResult<ScientificNameGroup> sngs = client.query(qs);
	    assertTrue("SNG found for taxon", sngs.size() > 0);
	    
	    boolean sngStringMatches = false;
	    // iterate over scientific name groups and identify the one that matches our taxon 
	    for (Iterator<QueryResultItem<ScientificNameGroup>> it2 = sngs.iterator(); it2.hasNext();) {
		ScientificNameGroup sng = it2.next().getItem();
		
		// test if the scientificNameGroup field from the taxon documents occurs in the names of the result SNGs
		if (sng.getName().equals(taxonSNG)) {
		    sngStringMatches = true;		    
		    break;
		}			    
	    }
	    assertTrue("SNG names match", sngStringMatches);	    
	}	
    }
    
    @Ignore("InvalidQueryException never thrown - due to design (@override)? ") // @Test(expected
										// =
    // InvalidQueryException.class)
    public void testQueryException() throws InvalidQueryException
    {
	/*
	 * Test the exception behavior of query. TODO: Ignored because behavior
	 * is not as expected!
	 */
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	// make invalid query
	qs.addCondition(new QueryCondition("nonsense", "=", "morenonsense"));
	client.query(qs);
    }

}
