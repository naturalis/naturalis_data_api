package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.mock.ScientificNameGroupMock.sngFelixFelix;
import static nl.naturalis.nba.dao.mock.ScientificNameGroupMock.sngLarusFuscus;
import static nl.naturalis.nba.dao.mock.ScientificNameGroupMock.sngLarusFuscusFuscus;
import static nl.naturalis.nba.dao.mock.ScientificNameGroupMock.sngMalusSylvestris;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.summary.SummarySpecimen;
import nl.naturalis.nba.dao.mock.ScientificNameGroupMock;

@SuppressWarnings("static-method")
public class ScientificNameGroupDaoTest {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(ScientificNameGroupDaoTest.class);

	@BeforeClass
	public static void Before()
	{
		logger.info("Initializing");
		ScientificNameGroupMock.saveAll();
		logger.info("Starting tests");
	}

	@Test
	public void testQuery_01()
	{
		//fail("Not yet implemented");
	}

	@Test
	public void testQuerySpecial_01() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		// constantScore *** true ***
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscus.getName());
		qs.addCondition(condition);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = sngLarusFuscus.getSpecimenCount();
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		// By default only 10 specimens per name group
		expected = 10;
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_02() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		// constantScore *** false ***
		qs.setConstantScore(false);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscus.getName());
		qs.addCondition(condition);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = sngLarusFuscus.getSpecimenCount();
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		// By default only 10 specimens per name group
		expected = 10;
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_03() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscus.getName());
		qs.addCondition(condition);
		// Lift the limit on the number of specimens per name group
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = sngLarusFuscus.getSpecimenCount();
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		expected = sngLarusFuscus.getSpecimens().size();
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_04() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscus.getName());
		qs.addCondition(condition);
		// Choose some other max size value
		qs.setSpecimensSize(2);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = sngLarusFuscus.getSpecimenCount();
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		expected = 2;
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_05() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscus.getName());
		qs.addCondition(condition);
		String field = "specimens.gatheringEvent.localityText";
		condition = new QueryCondition(field, "=", "Aalten");
		qs.addCondition(condition);
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = 5;
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_06() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngMalusSylvestris.getName());
		qs.addCondition(condition);
		String field = "specimens.gatheringEvent.localityText";
		condition = new QueryCondition(field, "=", "Rotterdam");
		qs.addCondition(condition);
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		assertEquals("01", 1, result.getTotalSize());
		int expected = 4;
		int actual = result.get(0).getItem().getSpecimenCount();
		assertEquals("02", expected, actual);
		actual = result.get(0).getItem().getSpecimens().size();
		assertEquals("03", expected, actual);
	}

	@Test
	public void testQuerySpecial_07() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngFelixFelix.getName());
		qs.addCondition(condition);
		qs.setSpecimensSize(-1);
		String field = "gatheringEvent.localityText";
		qs.setSpecimensSortFields(Arrays.asList(new SortField(field)));
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		List<SummarySpecimen> specimens = result.get(0).getItem().getSpecimens();
		assertEquals("01", 5, specimens.size());
		String locality = specimens.get(0).getGatheringEvent().getLocalityText();
		assertEquals("02", "Breda", locality);
		locality = specimens.get(1).getGatheringEvent().getLocalityText();
		assertEquals("03", "Breda", locality);
		locality = specimens.get(2).getGatheringEvent().getLocalityText();
		assertEquals("04", "Breda", locality);
		locality = specimens.get(3).getGatheringEvent().getLocalityText();
		assertEquals("05", "Rotterdam", locality);
		locality = specimens.get(4).getGatheringEvent().getLocalityText();
		assertEquals("06", "Rotterdam", locality);
	}

	@Test
	public void testQuerySpecial_08() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngFelixFelix.getName());
		qs.addCondition(condition);
		qs.setSpecimensSize(-1);
		String field = "gatheringEvent.localityText";
		qs.setSpecimensSortFields(Arrays.asList(new SortField(field, SortOrder.DESC)));
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		List<SummarySpecimen> specimens = result.get(0).getItem().getSpecimens();
		assertEquals("01", 5, specimens.size());
		String locality = specimens.get(0).getGatheringEvent().getLocalityText();
		assertEquals("02", "Rotterdam", locality);
		locality = specimens.get(1).getGatheringEvent().getLocalityText();
		assertEquals("03", "Rotterdam", locality);
		locality = specimens.get(2).getGatheringEvent().getLocalityText();
		assertEquals("04", "Breda", locality);
		locality = specimens.get(3).getGatheringEvent().getLocalityText();
		assertEquals("05", "Breda", locality);
		locality = specimens.get(4).getGatheringEvent().getLocalityText();
		assertEquals("06", "Breda", locality);
	}

	@Test
	public void testQuerySpecial_09() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscusFuscus.getName());
		qs.addCondition(condition);
		String field = "specimens.gatheringEvent.gatheringPersons.fullName";
		String value = "Kirsten Dunst";
		condition = new QueryCondition(field, "=", value);
		qs.addCondition(condition);
		qs.setSpecimensSortFields(Arrays.asList(new SortField("unitID", SortOrder.DESC)));
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		String unitID = null;
		List<SummarySpecimen> specimens = result.get(0).getItem().getSpecimens();
		for (SummarySpecimen specimen : specimens) {
			if (unitID != null) {
				assertTrue("00", unitID.compareTo(specimen.getUnitID()) >= 0);
				unitID = specimen.getUnitID();
			}
			assertEquals("01", value,
					specimen.getGatheringEvent().getGatheringPersons().get(0).getFullName());
		}
	}

	@Test
	public void testQuerySpecial_10() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscusFuscus.getName());
		qs.addCondition(condition);
		String field = "specimens.gatheringEvent.gatheringPersons.fullName";
		condition = new QueryCondition(field, "=", "Kirsten Dunst");
		condition.or(new QueryCondition(field, "=", "Robert Redford"));
		qs.addCondition(condition);
		qs.setSpecimensSortFields(Arrays.asList(new SortField("unitID")));
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		List<SummarySpecimen> specimens = result.get(0).getItem().getSpecimens();
		int size = specimens.size();
		qs.setSpecimensFrom(1);
		result = dao().querySpecial(qs);
		specimens = result.get(0).getItem().getSpecimens();
		assertEquals("01", size, specimens.size() + 1);
	}

	@Test
	public void testQuerySpecial_11() throws InvalidQueryException
	{
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setConstantScore(true);
		QueryCondition condition = new QueryCondition("name", "=", sngLarusFuscusFuscus.getName());
		qs.addCondition(condition);
		String field = "specimens.gatheringEvent.gatheringPersons.fullName";
		condition = new QueryCondition(field, "=", "Kirsten Dunst");
		condition.or(new QueryCondition(field, "=", "Robert Redford"));
		qs.addCondition(condition);
		qs.setSpecimensSortFields(Arrays.asList(new SortField("unitID")));
		qs.setSpecimensSize(-1);
		QueryResult<ScientificNameGroup> result = dao().querySpecial(qs);
		List<SummarySpecimen> specimens = result.get(0).getItem().getSpecimens();
		String unitID = specimens.get(1).getUnitID();
		qs.setSpecimensFrom(1);
		result = dao().querySpecial(qs);
		specimens = result.get(0).getItem().getSpecimens();
		assertEquals("01", unitID, specimens.get(0).getUnitID());
		unitID = specimens.get(1).getUnitID();
		qs.setSpecimensFrom(2);
		result = dao().querySpecial(qs);
		specimens = result.get(0).getItem().getSpecimens();
		assertEquals("02", unitID, specimens.get(0).getUnitID());
	}

	private static ScientificNameGroupDao dao()
	{
		return new ScientificNameGroupDao();
	}

}
