package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.SortOrder.DESC;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.dao.mock.SpecimenMock;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_SortingSizingPaging {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_SortingSizingPaging.class);

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		logger.info("Starting tests");
		deleteIndex(DocumentType.SPECIMEN);
		createIndex(DocumentType.SPECIMEN);
		/*
		 * Insert 5 test specimens.
		 */
		pMajor = SpecimenMock.parusMajorSpecimen01();
		lFuscus1 = SpecimenMock.larusFuscusSpecimen01();
		lFuscus2 = SpecimenMock.larusFuscusSpecimen02();
		tRex = SpecimenMock.tRexSpecimen01();
		mSylvestris = SpecimenMock.malusSylvestrisSpecimen01();
		DaoTestUtil.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@After
	public void after()
	{
	}

	/*
	 * Test with sort on non-nested field (ascending)
	 */
	@Test
	public void test__01() throws InvalidQueryException
	{
		List<Specimen> expected = sortByUnitIDAscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			String unitID = result.get(i).getItem().getUnitID();
			assertEquals("01", expected.get(i).getUnitID(), unitID);
		}
	}

	/*
	 * Test with sort on non-nested field (descending)
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		List<Specimen> expected = sortByUnitIDDesscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID", DESC);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			String unitID = result.get(i).getItem().getUnitID();
			assertEquals("01", expected.get(i).getUnitID(), unitID);
		}
	}

	/*
	 * Test with sort on nested field (ascending) without query conditions.
	 */
	@Test
	public void test__03() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName");
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			String unitID = result.get(i).getItem().getUnitID();
			assertEquals("01", expected.get(i).getUnitID(), unitID);
		}
	}

	/*
	 * Test with sort on nested field (descending) without query conditions.
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameDescending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName", DESC);
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			String unitID = result.get(i).getItem().getUnitID();
			assertEquals(("0" + (i + 2)), expected.get(i).getUnitID(), unitID);
		}
	}

	/*
	 * Test with sort on nested field (descending) and a single query condition
	 * on that same field.
	 */
	@Test
	public void test__05() throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, EQUALS, "Larus f. fuscus"));
		qs.sortBy(field, SortOrder.DESC);
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", result.size(), 2);
		/*
		 * Since they both have the same scientific name, the specimens are
		 * sorted on unitID (ascending).
		 */
		String expected = lFuscus2.getUnitID(); // "309801857"
		String actual = result.get(0).getItem().getUnitID();
		assertEquals("02", expected, actual);
		expected = lFuscus1.getUnitID(); // "ZMA.MAM.101"
		actual = result.get(1).getItem().getUnitID();
		assertEquals("03", expected, actual);
	}

	/*
	 * Test with sort on nested field with multiple query conditions on that
	 * same field. This needs testing because of the "nested_filter" that gets
	 * generated within the "sort" field of the Elasticsearch query.
	 */
	@Test
	public void test__06() throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, EQUALS, "Larus f. fuscus"));
		qs.addCondition(new QueryCondition(field, EQUALS, "Malus sylvestris"));
		qs.setLogicalOperator(LogicalOperator.OR);
		qs.sortBy(field, SortOrder.DESC);
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", result.size(), 3);
	}

	/*
	 * Sort on field in nested object while also having a condition on that
	 * field. The condition has a sibling that is on another field.
	 */
	@Test
	public void test__07() throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		QueryCondition condition = new QueryCondition(field, EQUALS, "Larus f. fuscus");
		// A bogus, superflous extra query condition
		QueryCondition sibling = new QueryCondition("recordBasis", NOT_EQUALS, "BLA BLA");
		condition.and(sibling);
		qs.addCondition(condition);
		qs.sortBy(field);
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", result.size(), 2);
		String expected = lFuscus2.getUnitID(); // "309801857"
		String actual = result.get(0).getItem().getUnitID();
		assertEquals("02", expected, actual);
		expected = lFuscus1.getUnitID(); // "ZMA.MAM.101"
		actual = result.get(1).getItem().getUnitID();
		assertEquals("03", expected, actual);
	}

	private static List<Specimen> sortByUnitIDAscending()
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				return o1.getUnitID().compareTo(o2.getUnitID());
			}
		});
		return specimens;
	}

	private static List<Specimen> sortByUnitIDDesscending()
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				return o2.getUnitID().compareTo(o1.getUnitID());
			}
		});
		return specimens;
	}

	private static List<Specimen> sortByScientificNameAscending()
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		for (int i = 0; i < specimens.size(); i++) {
			/*
			 * For each specimen, sort its identifications array according full
			 * scientific name. Thus, the first identification will be the one
			 * containing the alphabetically "lowest" full scientific name
			 */
			Collections.sort(specimens.get(i).getIdentifications(),
					new Comparator<SpecimenIdentification>() {

						@Override
						public int compare(SpecimenIdentification o1, SpecimenIdentification o2)
						{
							String fsn1 = o1.getScientificName().getFullScientificName();
							String fsn2 = o2.getScientificName().getFullScientificName();
							return fsn1.compareTo(fsn2);
						}
					});
		}
		/*
		 * Sort specimens in ascending order full scientific name of first
		 * identification
		 */
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				String fsn1 = o1.getIdentifications().get(0).getScientificName()
						.getFullScientificName();
				String fsn2 = o2.getIdentifications().get(0).getScientificName()
						.getFullScientificName();
				int i = fsn1.compareTo(fsn2);
				if (i == 0) {
					return o1.getUnitID().compareTo(o2.getUnitID());
				}
				return i;
			}
		});
		return specimens;
	}

	private static List<Specimen> sortByScientificNameDescending()
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		for (int i = 0; i < specimens.size(); i++) {
			/*
			 * For each specimen, sort its identifications array according full
			 * scientific name. Thus, the first identification will be the one
			 * containing the alphabetically "highest" full scientific name
			 */
			Collections.sort(specimens.get(i).getIdentifications(),
					new Comparator<SpecimenIdentification>() {

						@Override
						public int compare(SpecimenIdentification o1, SpecimenIdentification o2)
						{
							String fsn1 = o1.getScientificName().getFullScientificName();
							String fsn2 = o2.getScientificName().getFullScientificName();
							return fsn2.compareTo(fsn1);
						}
					});
		}
		/*
		 * Sort specimens in descending order of full scientific name of first
		 * identification
		 */
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				String fsn1 = o1.getIdentifications().get(0).getScientificName()
						.getFullScientificName();
				String fsn2 = o2.getIdentifications().get(0).getScientificName()
						.getFullScientificName();
				int i = fsn2.compareTo(fsn1);
				if (i == 0) {
					return o1.getUnitID().compareTo(o2.getUnitID());
				}
				return i;
			}
		});
		return specimens;
	}

}
