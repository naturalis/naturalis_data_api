package nl.naturalis.nba.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import java.util.Collections;
import java.util.Comparator;
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
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.summary.SummaryGatheringEvent;
import nl.naturalis.nba.api.model.summary.SummaryScientificName;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.api.model.summary.SummarySpecimenIdentification;

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
    public void testSpecimenInNameGroup() throws InvalidQueryException 
    {
	/* A ScientificNameGroup document can have associated specimens,
	 * represented by a thinner version of a Specimen (SummarySpecimen) document nested
	 * under the ScientificNameGroup. Here we test (for some of them) on 
	 * real data, if all fields of the Specimen in the ScientificNameGroup
	 * match the corresponding fields in the respective Specimen document*/
	SpecimenClient sc = session.getSpecimenClient();
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	
	// only select SNGs with one or more specimen
	qs.addCondition(new QueryCondition("specimenCount", ComparisonOperator.GT, 0));
	qs.setSize(100); // caution magic number
	QueryResult<ScientificNameGroup> result = client.query(qs);
	for (Iterator<QueryResultItem<ScientificNameGroup>> it1 = result.iterator(); it1.hasNext();) {
	    // extract summary specimens
	    for (Iterator<SummarySpecimen> it2 = it1.next().getItem().getSpecimens().iterator(); it2.hasNext();) {
		SummarySpecimen ssp = it2.next();
		String unitID = ssp.getUnitID();
		// query for corresponding specimen
		Specimen[] spresult = sc.findByUnitID(unitID);
		// there should be only one specimen
		assertEquals("Exactly one specimen found for unitID", spresult.length, 1);
		Specimen sp = spresult[0];
		// compare values of SpecimenSummary to Specimen object		
		assertEquals("Assemlage ID matches", ssp.getAssemblageID(), sp.getAssemblageID());
		assertEquals("Collection type matches", ssp.getCollectionType(), sp.getCollectionType());
		assertEquals("Collectors field number matches", ssp.getCollectorsFieldNumber(), sp.getCollectorsFieldNumber());
		assertEquals("Phase or stage matches", ssp.getPhaseOrStage(), sp.getPhaseOrStage());
		assertEquals("Sex matches", ssp.getSex(), sp.getSex());
		assertEquals("Unit ID matches", ssp.getUnitID(), sp.getUnitID());

		//assertTrue("Source system matches", ssp.getSourceSystem() == sp.getSourceSystem());
		// compare gathering events
		SummaryGatheringEvent sgev = ssp.getGatheringEvent();
		GatheringEvent gev = sp.getGatheringEvent();
		assertEquals("Gathering event begin matches", sgev.getDateTimeBegin(), gev.getDateTimeBegin());
		assertEquals("Loaclity text matches", sgev.getLocalityText(), gev.getLocalityText());
		//assertEquals("Number of gathering persons matches", sgev.getGatheringPersons().size(), gev.getGatheringPersons().size()); //caution, only comparing size 
		//assertEquals("Number of site coordinates matches", sgev.getSiteCoordinates().size(), gev.getSiteCoordinates().size());				

		// compare identifications		
		List<SummarySpecimenIdentification> mi = ssp.getMatchingIdentifications();
		List<SummarySpecimenIdentification> oi = ssp.getOtherIdentifications();
		List<SummarySpecimenIdentification> sident = mi;
		mi.addAll(oi);
		sortSummarySpecimenIdentifications(mi);		
		List<SpecimenIdentification> ident = sp.getIdentifications();		
		sortSpecimenIdentifications(ident);
		
		assertEquals(ident.size(), sident.size());
		
	    }	    
	}
    }
    
    // helper functions
    private static boolean matchIdentifications(SpecimenIdentification si, SummarySpecimenIdentification si2) {
	//TODO : Implement me
	return (true);	
    }
    
    private static void sortSpecimenIdentifications(List<SpecimenIdentification> identifications)
    {
	Collections.sort(identifications, new Comparator<SpecimenIdentification>() {
	    
	    @Override
	    public int compare(SpecimenIdentification si1, SpecimenIdentification si2)
	    {
		ScientificName sn1 = si1.getScientificName();
		ScientificName sn2 = si2.getScientificName();
		return sn1.getFullScientificName().compareTo(sn2.getFullScientificName());
	    }
			});
    }
      
    private static void sortSummarySpecimenIdentifications(List<SummarySpecimenIdentification> identifications)
    {
	Collections.sort(identifications, new Comparator<SummarySpecimenIdentification>() {
	    
	    @Override
	    public int compare(SummarySpecimenIdentification si1, SummarySpecimenIdentification si2)
	    {
		SummaryScientificName sn1 = si1.getScientificName();
		SummaryScientificName sn2 = si2.getScientificName();
		return sn1.getFullScientificName().compareTo(sn2.getFullScientificName());
	    }
			});
    }

    
    
    @Ignore("InvalidQueryException never thrown - due to design (@override)? ") // @Test(expected
										// =
								// InvalidQueryException.class)
    public void testQueryException() throws InvalidQueryException
    {
	/*
	 * Test the exception behavior of query. TODO: Ignored
	 * because behavior is not as expected!
	 */
	ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
	// make invalid query
	qs.addCondition(new QueryCondition("nonsense", "=", "morenonsense"));
	client.query(qs);
    }

}
