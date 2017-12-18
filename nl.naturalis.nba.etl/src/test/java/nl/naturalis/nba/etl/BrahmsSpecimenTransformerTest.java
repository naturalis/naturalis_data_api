/**
 * 
 */
package nl.naturalis.nba.etl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.etl.brahms.BrahmsCsvField;
import nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CSVRecordInfo.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings("unchecked")
public class BrahmsSpecimenTransformerTest {

    ETLStatistics etlStatistics;
    ScientificName expected;


    @Before
    public void setUp() throws Exception {

        String logFile = "log4j2.xml";
        URL logFileUrl = getClass().getResource(logFile);
        String logFilePath = logFileUrl.getFile().toString();
        String dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
        System.setProperty("nba.v2.conf.dir", dirPath);
        System.setProperty("brahms.data.dir", dirPath);
        System.setProperty("log4j.configurationFile", logFilePath);
        System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");

        etlStatistics = new ETLStatistics();
        etlStatistics.badInput = 0;
        etlStatistics.documentsIndexed = 0;
        etlStatistics.documentsRejected = 0;
        etlStatistics.setNested(false);
        etlStatistics.objectsAccepted = 0;
        etlStatistics.objectsProcessed = 0;
        etlStatistics.objectsRejected = 0;
        etlStatistics.objectsSkipped = 0;
        etlStatistics.setOneToMany(false);
        etlStatistics.recordsAccepted = 0;
        etlStatistics.recordsProcessed = 0;
        etlStatistics.recordsRejected = 0;
        etlStatistics.recordsSkipped = 0;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        etlStatistics = null;
    }

    /**
     * Generic Reflection method
     * 
     * @param CSVRecordInfo
     * @param brahmsSpecimenTransformer
     * @param methodName
     * 
     * @return Object
     * @throws Exception
     */
    private Object genericReflectionMethod(CSVRecordInfo<BrahmsCsvField> record, BrahmsSpecimenTransformer brahmsSpecimenTransformer,
            String methodName) throws Exception {
        Object object = null;
        Method method = null;
        if (record != null) {
            method = Class.forName(brahmsSpecimenTransformer.getClass().getName()).getDeclaredMethod(methodName, CSVRecordInfo.class);
            method.setAccessible(true);
            object = method.invoke(brahmsSpecimenTransformer, record);
        } else {
            method = Class.forName(brahmsSpecimenTransformer.getClass().getName()).getDeclaredMethod(methodName);
            method.setAccessible(true);
            object = method.invoke(brahmsSpecimenTransformer);
        }
        return object;
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformer#doTransform()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the do Transform object returns the correct {@List<Specimen>} object
     */

    @Test
    public void testDoTransform() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

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
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;


        Object returned = genericReflectionMethod(null, brahmsSpecimenTransformer, "doTransform");

        String expectedId = "L.3355550@BRAHMS";
        String expectedGUID = "http://data.biodiversitydata.nl/naturalis/specimen/L.3355550";

        List<Specimen> list = (List<Specimen>) returned;

        assertNotNull(list);
        assertTrue(list.size() == 1);
        assertEquals(expectedId, (list.stream().map(i -> i.getId()).findFirst().get()));
        assertEquals(expectedGUID, list.stream().map(i -> i.getUnitGUID()).findFirst().get());


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

        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

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
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;

        Object returned = genericReflectionMethod(record, brahmsSpecimenTransformer, "getGatheringEvent");

        GatheringEvent ge = (GatheringEvent) returned;

        String expectedContinent = "Europe";
        String expectedCountry = "Netherlands";

        assertNotNull(ge);
        assertEquals(expectedContinent, ge.getContinent());
        assertEquals(expectedCountry, ge.getCountry());

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

        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

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
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;

        Object returned = genericReflectionMethod(record, brahmsSpecimenTransformer, "getSpecimenIdentification");

        String expectedGenus = "Rhododendron";
        String expectedKingdom = "Plantae";
        String expectedSpecificEpithat = "ferrugineum";

        SpecimenIdentification identification = (SpecimenIdentification) returned;

        assertNotNull(identification);
        assertEquals(expectedGenus, identification.getDefaultClassification().getGenus());
        assertEquals(expectedSpecificEpithat, identification.getDefaultClassification().getSpecificEpithet());
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

        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;

        Object returned = genericReflectionMethod(null, brahmsSpecimenTransformer, "getAssemblageID");
        String expectedAssembleID = "1993139@BRAHMS";

        String assembleId = (String) returned;

        assertNotNull(assembleId);
        assertEquals(expectedAssembleID, assembleId);


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

        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;

        Object returned = genericReflectionMethod(null, brahmsSpecimenTransformer, "getCollectorsFieldNumber");

        String expectedCollectionNumber = "Unknown  s.n. ";

        String actualCollectorsNumber = (String) returned;

        assertNotNull(expectedCollectionNumber);
        assertEquals(expectedCollectionNumber, actualCollectorsNumber);

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


        CSVRecordInfo<BrahmsCsvField> record;
        record = PowerMockito.mock(CSVRecordInfo.class);
        expected = new ScientificName();

        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        brahmsSpecimenTransformer.objectID = "L.3355550";
        brahmsSpecimenTransformer.input = record;

        Object returned = genericReflectionMethod(null, brahmsSpecimenTransformer, "getServiceAccessPoints");
        List<ServiceAccessPoint> actualList = (List<ServiceAccessPoint>) returned;

        String expectedAccessPoint = "http://medialib.naturalis.nl/file/id/L.3355550/format/large";

        String actualAccessPoint = actualList.stream().map(i -> i.getAccessUri().toString()).findFirst().get();

        assertNotNull(returned);
        assertEquals(expectedAccessPoint, actualAccessPoint);


    }



}
