/**
 * 
 */
package nl.naturalis.nda.export;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringEvent;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import nl.naturalis.nda.export.dwca.CsvFileWriter;
import nl.naturalis.nda.export.dwca.Fieldmapping;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Reinier.Kartowikromo
 *
 */
public class FieldmappingTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	private CsvFileWriter.CsvRow dataRow;
	ESSpecimen specimen;
	ESGatheringEvent ge;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
//		dataRow = (new CsvFileWriter("C:/tmp/test.csv")).new CsvRow();
//		specimen = new ESSpecimen();
//		ge = new ESGatheringEvent();
//		specimen.setGatheringEvent(ge);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#main(java.lang.String[])}
	 * .
	 */
	@Test
	public void testMain() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setDummyValue(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetDummyValue() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setBasisOfRecord(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetBasisOfRecord() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setBasisOfRecord_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetBasisOfRecord_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setCatalogNumber(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetCatalogNumber() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setClassName(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetClassName() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setClassName_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetClassName_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setCollectionType(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetCollectionType() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setCollectionCode_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetCollectionCode_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setContinent(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetContinent() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setCountry(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetCountry() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setCounty(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetCounty() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setDateIndentified(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetDateIndentified() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setDateIndentified_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetDateIndentified_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setLatitudeDecimal(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetLatitudeDecimal() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setLongitudeDecimal(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetLongitudeDecimal() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setEvendate(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetEvendate() throws Exception {
//		dataRow.add("test");
//		Fieldmapping.setEvendate(specimen, dataRow);
//		assertEquals("Should be 2", 2, dataRow.size());
//		assertEquals("Should be empty", "", dataRow.get(1));
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setEvendate_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetEvendate_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setFamily(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetFamily() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setFamily_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetFamily_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setGenus(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetGenus() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setGenus_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetGenus_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setGeodeticDatum(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetGeodeticDatum() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setHabitat(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetHabitat() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setHigherClassification(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetHigherClassification() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setHigherClassification_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetHigherClassification_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setIdentifiersFullName(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetIdentifiersFullName() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setIdentifiersFullName_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetIdentifiersFullName_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setNumberOfSpecimen(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetNumberOfSpecimen() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setInformationWithHeld(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetInformationWithHeld() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setInfraspecificEpithet(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetInfraspecificEpithet() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setInfraspecificEpithet_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetInfraspecificEpithet_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setIsland(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetIsland() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setInstitudeCode(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetInstitudeCode() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setKingdom(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetKingdom() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setKingdom_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetKingdom_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setPhaseOrStage(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetPhaseOrStage() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setLocality(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetLocality() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setMaximumElevationInMeters(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetMaximumElevationInMeters() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setMinimumElevationInMeters(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetMinimumElevationInMeters() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setNomenclaturalCode_Zoology(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetNomenclaturalCode_Zoology() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setNomenclaturalCode_Geology(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetNomenclaturalCode_Geology() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setNomenclaturalCode_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetNomenclaturalCode_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setOccurrenceID(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetOccurrenceID() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setOrder(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetOrder() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setOrder_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetOrder_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setPhylum(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetPhylum() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setPreparationType(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetPreparationType() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setGatheringAgents_FullName(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetGatheringAgents_FullName() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setFullScientificName(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetFullScientificName() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setFullScientificName_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetFullScientificName_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setAuthorshipVerbatim(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetAuthorshipVerbatim() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setAuthorshipVerbatim_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetAuthorshipVerbatim_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setSex(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetSex() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setSpecificEpithet(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetSpecificEpithet() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setSpecificEpithet_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetSpecificEpithet_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setProvinceState(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetProvinceState() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setSubGenus(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetSubGenus() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setSubGenus_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetSubGenus_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setTaxonrank(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetTaxonrank() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setTaxonrank_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetTaxonrank_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setTaxonRemarks(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetTaxonRemarks() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setTypeStatus(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetTypeStatus() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setVerbatimCoordinates(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetVerbatimCoordinates() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setVerbatimCoordinates_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetVerbatimCoordinates_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setVerbatimDepth(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetVerbatimDepth() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setAltitudeUnifOfMeasurement(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetAltitudeUnifOfMeasurement() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setVerbatimEventDate(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetVerbatimEventDate() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setVerbatimEventDate_Brahms(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetVerbatimEventDate_Brahms() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link nl.naturalis.nda.export.dwca.Fieldmapping#setTaxonRank_Is_VerbatimTaxonRank(nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen, nl.naturalis.nda.export.dwca.CsvFileWriter.CsvRow)}
	 * .
	 */
	@Test
	public void testSetTaxonRank_Is_VerbatimTaxonRank() {
		// fail("Not yet implemented");
	}

}
