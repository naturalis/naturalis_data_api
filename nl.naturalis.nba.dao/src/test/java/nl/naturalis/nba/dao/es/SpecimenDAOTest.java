package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.api.query.Operator.EQUALS;
import static nl.naturalis.nba.dao.ESTestUtils.createIndex;
import static nl.naturalis.nba.dao.ESTestUtils.createType;
import static nl.naturalis.nba.dao.ESTestUtils.dropIndex;
import static nl.naturalis.nba.dao.ESTestUtils.refreshIndex;
import static nl.naturalis.nba.dao.ESTestUtils.saveObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Person;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAOTest {

	static final ESSpecimen specimen01 = new ESSpecimen();
	static final ESSpecimen specimen02 = new ESSpecimen();

	@BeforeClass
	public static void setup()
	{

		Person person = new Person("Wallich, N");
		ESGatheringEvent gathering = new ESGatheringEvent();
		gathering.setGatheringPersons(Arrays.asList(person));

		DefaultClassification dc = new DefaultClassification();
		dc.setGenus("Parus");
		dc.setSpecificEpithet("major");

		SpecimenIdentification si = new SpecimenIdentification();
		si.setDefaultClassification(dc);

		specimen01.setUnitID("ZMA.MAM.12345");
		specimen01.setGatheringEvent(gathering);
		specimen01.setIdentifications(Arrays.asList(si));

		specimen02.setUnitID("L  0000123");
	}

	@Before
	public void before()
	{
		dropIndex(ESSpecimen.class);
		createIndex(ESSpecimen.class);
		createType(ESSpecimen.class);
	}

	@After
	public void after()
	{
		//dropIndex(ESSpecimen.class);
	}

	//@Test
	public void testFindById_1()
	{
		String id = "ZMA.MAM.12345@CRS";
		saveObject(id, specimen01);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		Specimen out = dao.findById(id);
		assertNotNull("01", out);
	}

	//@Test
	public void testFindByUnitID_1()
	{
		String unitID = specimen01.getUnitID();
		saveObject(specimen01);
		saveObject(specimen01);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.findByUnitID(unitID);
		assertNotNull("01", result);
		assertEquals("02", 2, result.size());
		assertEquals("03", unitID, result.get(0).getUnitID());
		assertEquals("04", unitID, result.get(1).getUnitID());
		// Make sure no weird analysis stuff is going on
		result = dao.findByUnitID("ZMA");
		assertNotNull("05", result);
		assertEquals("06", 0, result.size());
		result = dao.findByUnitID("12345");
		assertNotNull("07", result);
		assertEquals("08", 0, result.size());
	}

	//@Test
	public void testFindByUnitID_2()
	{
		String unitID0 = specimen02.getUnitID();
		saveObject(specimen02);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.findByUnitID(unitID0);
		assertNotNull("01", result);
		assertEquals("02", 1, result.size());
		assertEquals("03", unitID0, result.get(0).getUnitID());
		// Make sure no weird analysis stuff is going on
		result = dao.findByUnitID("L");
		assertNotNull("04", result);
		assertEquals("05", 0, result.size());
	}

	//@Test
	public void testFindByUnitID_3()
	{
		ESSpecimen specimen = new ESSpecimen();
		specimen.setUnitID("A");
		saveObject(specimen);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.findByUnitID("NOT A");
		assertNotNull("01", result);
		assertEquals("02", 0, result.size());
	}

	//@Test
	public void testFindByCollector_1()
	{
		saveObject("ZMA.MAM.12345@CRS", specimen01);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		Person person = specimen01.getGatheringEvent().getGatheringPersons().get(0);
		String collector = person.getFullName();
		List<Specimen> result = dao.findByCollector(collector);
		assertNotNull("01", result);
		assertNotNull("02", result.get(0));
		assertNotNull("03", result.get(0).getGatheringEvent());
		assertNotNull("04", result.get(0).getGatheringEvent().getGatheringAgents());
		Agent agent = result.get(0).getGatheringEvent().getGatheringAgents().get(0);
		assertNotNull("05", agent);
		assertEquals("06", Person.class, agent.getClass());
		Person personOut = (Person) agent;
		assertEquals("07", person, personOut);
	}

	//@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		saveObject(specimen01);
		refreshIndex(ESSpecimen.class);
		String unitID = specimen01.getUnitID();
		Condition condition = new Condition("unitID", EQUALS, unitID);
		QuerySpec qs = new QuerySpec();
		qs.setCondition(condition);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
		assertEquals("02", specimen01.getUnitID(), result.get(0).getUnitID());
	}

	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		saveObject(specimen01);
		String genus = "identifications.defaultClassification.genus";
		String specificEpithet = "identifications.defaultClassification.specificEpithet";
		Condition condition = new Condition(genus, EQUALS, "Parus");
		condition.and(specificEpithet, EQUALS, "major");
		QuerySpec qs = new QuerySpec();
		qs.setCondition(condition);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.query(qs);

	}

}
