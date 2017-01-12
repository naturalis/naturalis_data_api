package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;
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

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

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

	@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				return o1.getUnitID().compareTo(o2.getUnitID());
			}
		});
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < specimens.size(); i++) {
			assertEquals("01", specimens.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		Collections.sort(specimens, new Comparator<Specimen>() {

			@Override
			public int compare(Specimen o1, Specimen o2)
			{
				return o2.getUnitID().compareTo(o1.getUnitID());
			}
		});
		QuerySpec qs = new QuerySpec();
		qs.sortBy("unitID", false);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < specimens.size(); i++) {
			assertEquals("01", specimens.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	@Test
	public void testQuery__QuerySpec__03() throws InvalidQueryException
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		for (int i = 0; i < specimens.size(); i++) {
			/*
			 * Sort identifications according full scientific name; first
			 * identification will be the one containing the alphabetically
			 * "lowest" full scientific name
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
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < specimens.size(); i++) {
			assertEquals("01", specimens.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	@Test
	public void testQuery__QuerySpec__04() throws InvalidQueryException
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		for (int i = 0; i < specimens.size(); i++) {
			/*
			 * Sort identifications according full scientific name; first
			 * identification will be the one containing the alphabetically
			 * "highest" full scientific name
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
		QuerySpec qs = new QuerySpec();
		qs.sortBy("identifications.scientificName.fullScientificName", false);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < specimens.size(); i++) {
			assertEquals("01", specimens.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

	@Test
	public void testQuery__QuerySpec__05() throws InvalidQueryException
	{
		List<Specimen> specimens = Arrays.asList(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
		for (int i = 0; i < specimens.size(); i++) {
			/*
			 * Sort identifications according full scientific name; first
			 * identification will be the one containing the alphabetically
			 * "lowest" full scientific name
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
		QuerySpec qs = new QuerySpec();
		String field = "identifications.scientificName.fullScientificName";
		qs.addCondition(new Condition(field, LIKE, "Larus"));
		qs.sortBy("identifications.scientificName.fullScientificName");
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		for (int i = 0; i < specimens.size(); i++) {
			assertEquals("01", specimens.get(i).getUnitID(), result.get(i).getUnitID());
		}
	}

}
