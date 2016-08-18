package nl.naturalis.nba.dao.es.format.dwca;

import static nl.naturalis.nba.dao.es.DocumentType.SPECIMEN;

import org.junit.Test;
import static org.junit.Assert.*;

import nl.naturalis.nba.dao.es.exception.DwcaCreationException;
import nl.naturalis.nba.dao.es.format.DataSetCollection;
import nl.naturalis.nba.dao.es.format.IDataSetField;
import nl.naturalis.nba.dao.es.format.NoSuchDataSetException;
import nl.naturalis.nba.dao.es.format.dwca.DwcaDataSetCollectionBuilder;

@SuppressWarnings("static-method")
public class DwcaDataSetCollectionBuilderTest {

	@Test
	public void testBuild_01()
	{
		DwcaDataSetCollectionBuilder builder;
		builder = new DwcaDataSetCollectionBuilder(SPECIMEN, "test-data-set-01");
		DataSetCollection dsc = builder.build();
		assertEquals("01", "test-collection-01", dsc.getName());
		assertEquals("02", SPECIMEN, dsc.getDocumentType());
		assertEquals("03", 1, dsc.getEntities().length);
		IDataSetField[] fields = dsc.getEntities()[0].getFields();
		assertEquals("04", 4, fields.length);
		assertEquals("05", "id", fields[0].getName());
		assertEquals("06", "scientificName", fields[1].getName());
		assertEquals("07", "scientificNameAuthorship", fields[2].getName());
		assertEquals("08", "basisOfRecord", fields[3].getName());
		assertEquals("09", "occurrence", dsc.getEntities()[0].getName());
	}

	@Test(expected = NoSuchDataSetException.class)
	public void testBuild_02()
	{
		DwcaDataSetCollectionBuilder builder;
		builder = new DwcaDataSetCollectionBuilder(SPECIMEN, "foo");
		builder.build();
	}

	@Test
	public void testBuild_03()
	{
		DwcaDataSetCollectionBuilder builder;
		builder = new DwcaDataSetCollectionBuilder(SPECIMEN, "test-data-set-02");
		try {
			builder.build();
		}
		catch (DwcaCreationException e) {
			String msg = "Duplicate data set \"test-data-set-02\" found under";
			assertTrue("01", e.getMessage().startsWith(msg));
			return;
		}
		fail("Expected duplicate data set error");
	}

}
