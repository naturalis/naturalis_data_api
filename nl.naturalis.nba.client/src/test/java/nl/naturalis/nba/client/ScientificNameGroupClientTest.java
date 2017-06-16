package nl.naturalis.nba.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.IScientificNameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;

public class ScientificNameGroupClientTest {

    // define global variables used by all tests
    // private String baseUrl = "http://localhost:8080/v2";
    private String baseUrl = "http://145.136.242.164:8080/v2";
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
    public void testSpecimenInNameGroup() throws InvalidQueryException
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
