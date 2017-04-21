package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;

import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.SpecimenMock;

@SuppressWarnings("static-method")
public class SpecimenDao_AggretationsTest {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDao_AggretationsTest.class);

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
		createType(DocumentType.SPECIMEN);
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

	@Test
	public void testGroup_01() throws InvalidQueryException
	{
		SpecimenDao dao = new SpecimenDao();
		dao.getGroups("recordBasis", null);
	}

}
