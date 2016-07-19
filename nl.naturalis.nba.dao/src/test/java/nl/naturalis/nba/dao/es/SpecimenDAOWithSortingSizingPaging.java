package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.util.ESUtil.createIndex;
import static nl.naturalis.nba.dao.es.util.ESUtil.createType;
import static nl.naturalis.nba.dao.es.util.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAOWithSortingSizingPaging {

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
		SpecimenDAO dao = new SpecimenDAO();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 5, result.length);
	}


}
