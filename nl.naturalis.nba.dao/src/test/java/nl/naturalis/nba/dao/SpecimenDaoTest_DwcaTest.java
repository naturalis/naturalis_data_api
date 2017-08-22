package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.SpecimenMock;
import nl.naturalis.nba.utils.debug.DevNullOutputStream;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_DwcaTest {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_DwcaTest.class);

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		logger.info("Start");
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
		// dropIndex(Specimen.class);
	}

	/*
	 * Just make sure we don't get exceptions. No assertions about contents of
	 * zip archive yet (TODO).
	 */
	@Test
	public void testDynamic() throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		String field = "identifications.defaultClassification.genus";
		String[] values = new String[] { "Parus", "Larus", "Malus" };
		qs.addCondition(new QueryCondition(field, "IN", values));
		SpecimenDao dao = new SpecimenDao();
		dao.dwcaQuery(qs, new DevNullOutputStream());
	}

}
