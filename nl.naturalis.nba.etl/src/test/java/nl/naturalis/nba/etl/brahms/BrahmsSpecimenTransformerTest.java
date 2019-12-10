package nl.naturalis.nba.etl.brahms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;

/**
 * Test class for BrahmsSpecimenTransformer.java
 */
@SuppressWarnings({"unchecked"})
public class BrahmsSpecimenTransformerTest {

    /**
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#doTransform()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the do Transform object returns the correct {@List<Specimen>} object
     */
    @Test
    public void testDoTransform() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(BrahmsCsvField.AUTHOR2)).thenReturn("L.");
        when(record.get(BrahmsCsvField.GENUS)).thenReturn("Rhododendron");
        when(record.get(BrahmsCsvField.SP1)).thenReturn("ferrugineum");
        when(record.get(BrahmsCsvField.RANK2)).thenReturn("testInfraSpecifc");
        when(record.get(BrahmsCsvField.SP2)).thenReturn("");
        when(record.get(BrahmsCsvField.BARCODE)).thenReturn("L.3355550");
        when(record.get(BrahmsCsvField.NOTONLINE)).thenReturn("");
        when(record.get(BrahmsCsvField.PLANTDESC)).thenReturn("TestDesc");
        when(record.get(BrahmsCsvField.CATEGORY)).thenReturn("L");
        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.TYPE)).thenReturn("");
        when(record.get(BrahmsCsvField.DETBY)).thenReturn("");
        when(record.get(BrahmsCsvField.VERNACULAR)).thenReturn("");
        when(record.get(BrahmsCsvField.YEARIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTHIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.DAYIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");
        when(record.get(BrahmsCsvField.CONTINENT)).thenReturn("Europe");
        when(record.get(BrahmsCsvField.COUNTRY)).thenReturn("Netherlands");
        when(record.get(BrahmsCsvField.MAJORAREA)).thenReturn("");
        when(record.get(BrahmsCsvField.LOCNOTES)).thenReturn("Io een rotsachtige rij vochtige berghelling bij het Kolmhans.");
        when(record.get(BrahmsCsvField.YEAR)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTH)).thenReturn("      8.000000");
        when(record.get(BrahmsCsvField.DAY)).thenReturn("      3.000000");
        when(record.get(BrahmsCsvField.LATITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.LONGITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.OLDBARCODE, true)).thenReturn("L  0020601");
        when(record.get(BrahmsCsvField.ACCESSION, true)).thenReturn("125784");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);

        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object returned = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "doTransform");

        List<Specimen> list = (List<Specimen>) returned;

        String expectedId = "L.3355550@BRAHMS";
        String expectedGUID = "https://data.biodiversitydata.nl/naturalis/specimen/L.3355550";
        String expectedFullScientificName = "Rhododendron ferrugineum L.";
        String expectedScientificNameGroup = "rhododendron ferrugineum ";
        String expectedGenusMonomial = "Rhododendron";
        String expectedPreviousUnitsTexts = "L  0020601 | 125784";
               
        assertNotNull("01",list);
        assertTrue("02",list.size() == 1);
        assertEquals("03",expectedId, list.stream().map(i -> i.getId()).findFirst().get());
        assertEquals("04",expectedGUID, list.stream().map(i -> i.getUnitGUID()).findFirst().get());        
        assertEquals("05",expectedFullScientificName,list.stream().map(i -> i.getIdentifications().get(0).getScientificName().getFullScientificName()).findFirst().get());       
        assertEquals("06",expectedScientificNameGroup,list.stream().map(i -> i.getIdentifications().get(0).getScientificName().getScientificNameGroup()).findFirst().get());
        assertEquals("07",expectedGenusMonomial, list.stream().map(i -> i.getIdentifications().get(0).getScientificName().getGenusOrMonomial()).findFirst().get());
        assertEquals("08", expectedPreviousUnitsTexts, list.stream().map(i -> i.getPreviousUnitsText()).findFirst().get());
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#gatheringEvent())}.
     * 
     * @throws Exception
     * 
     *         Test to verify gathering event object returned.
     */
    @Test
    public void testGatheringEvent() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(BrahmsCsvField.AUTHOR2)).thenReturn("L.");
        when(record.get(BrahmsCsvField.GENUS)).thenReturn("Rhododendron");
        when(record.get(BrahmsCsvField.SP1)).thenReturn("ferrugineum");
        when(record.get(BrahmsCsvField.RANK2)).thenReturn("testInfraSpecifc");
        when(record.get(BrahmsCsvField.SP2)).thenReturn("");
        when(record.get(BrahmsCsvField.BARCODE)).thenReturn("L.3355550");
        when(record.get(BrahmsCsvField.NOTONLINE)).thenReturn("");
        when(record.get(BrahmsCsvField.PLANTDESC)).thenReturn("TestDesc");
        when(record.get(BrahmsCsvField.CATEGORY)).thenReturn("L");
        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.TYPE)).thenReturn("");
        when(record.get(BrahmsCsvField.DETBY)).thenReturn("");
        when(record.get(BrahmsCsvField.VERNACULAR)).thenReturn("");
        when(record.get(BrahmsCsvField.YEARIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTHIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.DAYIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");
        when(record.get(BrahmsCsvField.CONTINENT)).thenReturn("Europe");
        when(record.get(BrahmsCsvField.COUNTRY)).thenReturn("Netherlands");
        when(record.get(BrahmsCsvField.MAJORAREA)).thenReturn("");
        when(record.get(BrahmsCsvField.LOCNOTES)).thenReturn("Io een rotsachtige rij vochtige berghelling bij het Kolmhans.");
        when(record.get(BrahmsCsvField.YEAR)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTH)).thenReturn("      8.000000");
        when(record.get(BrahmsCsvField.DAY)).thenReturn("      3.000000");
        when(record.get(BrahmsCsvField.LATITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.LONGITUDE)).thenReturn("      0.000000");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);

        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = CommonReflectionUtil.callMethod(record, CSVRecordInfo.class, brahmsSpecimenTransformer, "getGatheringEvent");
        GatheringEvent ge = (GatheringEvent) obj;

        String expectedContinent = "Europe";
        String expectedCountry = "Netherlands";

        assertNotNull("01",ge);
        assertEquals("02",expectedContinent, ge.getContinent());
        assertEquals("03",expectedCountry, ge.getCountry());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#getSpecimenIdentification())}.
     * 
     * @throws Exception
     * 
     *         Test to verify the specimen identification object returned.
     */
    @Test
    public void testGetSpecimenIdentification() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(BrahmsCsvField.AUTHOR2)).thenReturn("L.");
        when(record.get(BrahmsCsvField.GENUS)).thenReturn("Rhododendron");
        when(record.get(BrahmsCsvField.SP1)).thenReturn("ferrugineum");
        when(record.get(BrahmsCsvField.RANK2)).thenReturn("testInfraSpecifc");
        when(record.get(BrahmsCsvField.SP2)).thenReturn("");
        when(record.get(BrahmsCsvField.BARCODE)).thenReturn("L.3355550");
        when(record.get(BrahmsCsvField.NOTONLINE)).thenReturn("");
        when(record.get(BrahmsCsvField.PLANTDESC)).thenReturn("TestDesc");
        when(record.get(BrahmsCsvField.CATEGORY)).thenReturn("L");
        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.TYPE)).thenReturn("");
        when(record.get(BrahmsCsvField.DETBY)).thenReturn("");
        when(record.get(BrahmsCsvField.VERNACULAR)).thenReturn("");
        when(record.get(BrahmsCsvField.YEARIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTHIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.DAYIDENT)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");
        when(record.get(BrahmsCsvField.CONTINENT)).thenReturn("Europe");
        when(record.get(BrahmsCsvField.COUNTRY)).thenReturn("Netherlands");
        when(record.get(BrahmsCsvField.MAJORAREA)).thenReturn("");
        when(record.get(BrahmsCsvField.LOCNOTES)).thenReturn("Io een rotsachtige rij vochtige berghelling bij het Kolmhans.");
        when(record.get(BrahmsCsvField.YEAR)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTH)).thenReturn("      8.000000");
        when(record.get(BrahmsCsvField.DAY)).thenReturn("      3.000000");
        when(record.get(BrahmsCsvField.LATITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.LONGITUDE)).thenReturn("      0.000000");
        
        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);

        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);
        Object obj = CommonReflectionUtil.callMethod(record, CSVRecordInfo.class, brahmsSpecimenTransformer, "getSpecimenIdentification");
        SpecimenIdentification identification = (SpecimenIdentification) obj;

        String expectedGenus = "Rhododendron";
        String expectedKingdom = "Plantae";
        String expectedSpecificEpithet = "ferrugineum";

        assertNotNull("01",identification);
        assertEquals("02",expectedGenus, identification.getDefaultClassification().getGenus());
        assertEquals("03",expectedSpecificEpithet, identification.getDefaultClassification().getSpecificEpithet());
        assertEquals(expectedKingdom, identification.getDefaultClassification().getKingdom());
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#getAssemblageID())}.
     * 
     * @throws Exception
     * 
     *         Test to verify the assembleId object returned.
     */
    @Test
    public void testGetAssemblageID() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);

        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getAssemblageID");
        String assembleId = (String) obj;

        String expectedAssembleID = "1993139@BRAHMS";

        assertNotNull("01",assembleId);
        assertEquals("02",expectedAssembleID, assembleId);
    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#getCollectorsFieldNumber())}.
     * 
     * @throws Exception
     * 
     *         Test to verify the collection field number returned.
     */
    @Test
    public void testGetCollectorsFieldNumber() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getCollectorsFieldNumber");
        String actualCollectorsNumber = (String) obj;

        String expectedCollectionNumber = "Unknown  s.n. ";

        assertNotNull("01",expectedCollectionNumber);
        assertEquals("02",expectedCollectionNumber, actualCollectorsNumber);

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#getServiceAccessPoints())}.
     * 
     * @throws Exception
     * 
     *         Test to verify the returned service access points
     */
    @Test
    public void testGetServiceAccessPoints() throws Exception {


        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);


        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getServiceAccessPoints");

        List<ServiceAccessPoint> actualList = (List<ServiceAccessPoint>) obj;

        String expectedAccessPoint = "https://medialib.naturalis.nl/file/id/L.3355550/format/large";

        String actualAccessPoint = actualList.stream().map(i -> i.getAccessUri().toString()).findFirst().get();

        assertNotNull("01",actualList);
        assertEquals("02",expectedAccessPoint, actualAccessPoint);
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#getPreviousUnitsText())}.
     * 
     * @throws Exception
     * 
     *         Test to verify values returned by getPreviousUnitsText()
     */
    @Test
    public void testGetPreviousUnitsText() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = mock(CSVRecordInfo.class);
        ETLStatistics etlStatistics = new ETLStatistics();
        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        when(record.get(BrahmsCsvField.OLDBARCODE, true)).thenReturn("L  0020601");
        when(record.get(BrahmsCsvField.ACCESSION, true)).thenReturn("125784");        
        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getPreviousUnitsText");
        String previousUnitsText = (String) obj;
        String expectedPreviousUnitsText = "L  0020601 | 125784";
        assertNotNull("01", previousUnitsText);
        assertEquals( "02", expectedPreviousUnitsText, previousUnitsText);

        when(record.get(BrahmsCsvField.OLDBARCODE, true)).thenReturn("L  0020601");
        when(record.get(BrahmsCsvField.ACCESSION, true)).thenReturn(null);
        obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getPreviousUnitsText");
        previousUnitsText = (String) obj;
        expectedPreviousUnitsText = "L  0020601";
        assertNotNull("03", previousUnitsText);
        assertEquals( "04", expectedPreviousUnitsText, previousUnitsText);

        when(record.get(BrahmsCsvField.OLDBARCODE, true)).thenReturn(null);
        when(record.get(BrahmsCsvField.ACCESSION, true)).thenReturn("125784");
        obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getPreviousUnitsText");
        previousUnitsText = (String) obj;
        expectedPreviousUnitsText = "125784";
        assertNotNull("05", previousUnitsText);
        assertEquals( "06", expectedPreviousUnitsText, previousUnitsText);
        
        when(record.get(BrahmsCsvField.OLDBARCODE, true)).thenReturn(" L  0020601 ");
        when(record.get(BrahmsCsvField.ACCESSION, true)).thenReturn(" ");
        obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getPreviousUnitsText");
        previousUnitsText = (String) obj;
        expectedPreviousUnitsText = "L  0020601";
        assertNotNull("07", previousUnitsText);
        assertEquals( "08", expectedPreviousUnitsText, previousUnitsText);
    }

}
