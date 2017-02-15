package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
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

import static nl.naturalis.nba.api.SortOrder.*;

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
		qs.sortBy("unitID", DESC);
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
		qs.sortBy("unitID");
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
		qs.sortBy("identifications.scientificName.fullScientificName", DESC);
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < result.size(); i++) {
			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

//	/*
//	 * Test with sort on nested field (descending) with a single query condition
//	 * on that same field.
//	 */
//	@Test
//	public void testQuery__QuerySpec__05() throws InvalidQueryException
//	{
//		List<Specimen> expected = sortByScientificNameDescending();
//		QuerySpec qs = new QuerySpec();
//		String field = "identifications.scientificName.fullScientificName";
//		qs.addCondition(new QueryCondition(field, EQUALS, "Larus f. fuscus"));
//		qs.sortBy(field, false);
//		qs.sortBy("unitID");
//		SpecimenDao dao = new SpecimenDao();
//		QueryResult<Specimen> result = dao.query(qs);
//		assertEquals("01",result.size(),2);
//		for (int i = 0; i < result.size(); i++) {
//			assertEquals(("0"+(i+2)), expected.get(i).getUnitID(), result.get(i).getUnitID());
//		}
//	}

//	/*
//	 * Test with sort on nested field with a multiple query conditions (joined
//	 * with OR) on that same field. This is worth testing because of the
//	 * "nested_filter" that gets generated within the "sort" field of the
//	 * Elasticsearch query.
//	 */
//	@Test
//	public void testQuery__QuerySpec__06() throws InvalidQueryException
//	{
//		List<Specimen> expected = sortByScientificNameDescending();
//		QuerySpec qs = new QuerySpec();
//		String field = "identifications.scientificName.fullScientificName";
//		/*
//		 * A bogus query (the 2nd condition is superfluous)
//		 */
//		qs.addCondition(new QueryCondition(field, EQUALS, "Larus f. fuscus"));
//		qs.addCondition(new QueryCondition(field, NOT_EQUALS, "Malus sylvestris"));
//		qs.setLogicalOperator(LogicalOperator.OR);
//		qs.sortBy(field, false);
//		qs.sortBy("unitID");
//		SpecimenDao dao = new SpecimenDao();
//		QueryResult<Specimen> result = dao.query(qs);
//		for (int i = 0; i < result.size(); i++) {
//			assertEquals("01", expected.get(i).getUnitID(), result.get(i).getUnitID());
//		}
//	}

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
