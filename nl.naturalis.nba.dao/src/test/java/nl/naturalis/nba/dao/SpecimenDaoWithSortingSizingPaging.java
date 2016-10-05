package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.util.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.ESUtil.createType;
import static nl.naturalis.nba.dao.util.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.dao.types.ESSpecimen;

@SuppressWarnings("static-method")
public class SpecimenDaoWithSortingSizingPaging {

	static ESSpecimen pMajor;
	static ESSpecimen lFuscus1;
	static ESSpecimen lFuscus2;
	static ESSpecimen tRex;
	static ESSpecimen mSylvestris;

	@Before
	public void before()
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
		QuerySpec qs = new QuerySpec();
		qs.sortAcending("city");
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 5, result.length);
	}


}
