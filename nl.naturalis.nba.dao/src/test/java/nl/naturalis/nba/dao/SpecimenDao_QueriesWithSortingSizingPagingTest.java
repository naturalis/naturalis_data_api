package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;

@SuppressWarnings("static-method")
public class SpecimenDao_QueriesWithSortingSizingPagingTest {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		deleteIndex(DocumentType.SPECIMEN);
		createIndex(DocumentType.SPECIMEN);
		createType(DocumentType.SPECIMEN);
		/*
		 * Insert 5 test specimens.
		 */
		pMajor = TestSpecimens.parusMajorSpecimen01();
		lFuscus1 = TestSpecimens.larusFuscusSpecimen01();
		lFuscus2 = TestSpecimens.larusFuscusSpecimen02();
		tRex = TestSpecimens.tRexSpecimen01();
		mSylvestris = TestSpecimens.malusSylvestrisSpecimen01();
		ESTestUtils.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@After
	public void after()
	{
	}

	/*
	 * Test with sort on non-nested field (ascending)
	 */
	@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		List<Specimen> expected = sortByUnitIDAscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on non-nested field (descending)
	 */
	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		List<Specimen> expected = sortByUnitIDDesscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID", false);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (ascending) without query conditions.
	 */
	@Test
	public void testQuery__QuerySpec__03() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with query conditions.
	 */
	@Test
	public void testQuery__QuerySpec__04() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameDescending();
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName", false);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (ascending) with a single query conditions
	 * on that same field.
	 */
	@Test
	public void testQuery__QuerySpec__05() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, LIKE, "Larus"));
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a multiple query
	 * conditions (joined with AND) on that same field.
	 */
	@Test
	public void testQuery__QuerySpec__06() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, LIKE, "Larus"));
		qs.addCondition(new QueryCondition(field, MATCHES, "Larus"));
		qs.setLogicalOperator(LogicalOperator.AND);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a multiple query
	 * conditions (joined with OR) on that same field.
	 */
	@Test
	public void testQuery__QuerySpec__07() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, LIKE, "Larus"));
		qs.addCondition(new QueryCondition(field, MATCHES, "Larus"));
		qs.setLogicalOperator(LogicalOperator.OR);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a single query condition
	 * containing an AND sibling condition, both on the same nested field.
	 */
	@Test
	public void testQuery__QuerySpec__08() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		QueryCondition condition = new QueryCondition(field, LIKE, "Larus");
		condition.and(new QueryCondition(field, MATCHES, "Larus"));
		qs.addCondition(condition);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a multiple query
	 * conditions, some on the same nested field, others on a different field.
	 * Should not be a problem; only the conditions on the nested field should
	 * end up in the "nested_filter" within the sort field (see log output).
	 */
	@Test
	public void testQuery__QuerySpec__09() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new QueryCondition(field, LIKE, "Larus"));
		qs.addCondition(new QueryCondition(field, MATCHES, "Larus"));
		qs.addCondition(new QueryCondition("unitID", NOT_EQUALS, "XXX.YYY.12345"));
		qs.setLogicalOperator(LogicalOperator.AND);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a single query condition
	 * on the same nested field, but the condition contains an OR sibling on a
	 * different field (unitID). Now we expect an exception!
	 */
	@Test(expected = InvalidQueryException.class)
	public void testQuery__QuerySpec__10() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		QueryCondition condition = new QueryCondition(field, LIKE, "Larus");
		condition.and(new QueryCondition(field, MATCHES, "Larus"));
		condition.or(new QueryCondition("unitID", NOT_EQUALS, "XXX.YYY.12345"));
		qs.addCondition(condition);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	/*
	 * Test with sort on nested field (descending) with a single query condition
	 * on the same nested field, but the condition contains a deeply nested
	 * sibling on a different field (unitID). Now we expect an exception!
	 */
	@Test(expected = InvalidQueryException.class)
	public void testQuery__QuerySpec__11() throws InvalidQueryException
	{
		List<Specimen> expected = sortByScientificNameAscending();
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		QueryCondition root = new QueryCondition(field, LIKE, "Larus");
		QueryCondition nested = new QueryCondition(field, MATCHES, "Larus");
		nested.or(new QueryCondition("unitID", NOT_EQUALS, "XXX.YYY.12345"));
		root.and(nested);
		qs.addCondition(root);
		qs.sortBy(field);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
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
				return fsn1.compareTo(fsn2);
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
				return fsn2.compareTo(fsn1);
			}
		});
		return specimens;
	}

}
