
package nl.naturalis.nba.etl.brahms;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
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
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * Test class for BrahmsSpecimenTransformer.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CSVRecordInfo.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method", "unchecked"})
public class BrahmsSpecimenTransformerTest {


    @Before
    public void setUp() throws Exception {

        String logFile = "log4j2.xml";
        URL logFileUrl = AllTests.class.getResource(logFile);
        String logFilePath = logFileUrl.getFile().toString();
        String dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
        System.setProperty("nba.v2.conf.dir", dirPath);
        System.setProperty("brahms.data.dir", dirPath);
        System.setProperty("log4j.configurationFile", logFilePath);
        System.setProperty("nl.naturalis.nba.etl.testGenera", "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {}

    /**
     * Generic Reflection method to access private methods.
     * 
     * @param Object
     * @param Object
     * @param Object
     * 
     * @return <T>
     * @throws Exception
     */

    private <T> T genericReflectionMethod(Object param, Class<T> paramClass, Object objectType, Object methodname) throws Exception {
        T obj = null;
        Method method = null;
        if (param != null) {
            method = Class.forName(objectType.getClass().getName()).getDeclaredMethod(methodname.toString(), paramClass);
            method.setAccessible(true);
            obj = (T) method.invoke(objectType, param);
        } else {
            method = Class.forName(objectType.getClass().getName()).getDeclaredMethod(methodname.toString());
            method.setAccessible(true);
            obj = (T) method.invoke(objectType);
        }
        return obj;
    }


    /**
     * Generic Reflection method to access private filed.
     * 
     * @param <T>
     * 
     * @param CSVRecordInfo
     * @param brahmsSpecimenTransformer
     * @param methodName
     * 
     * @return Object
     * @throws Exception
     */

    private <T> void setPrivateField(Class<T> className, Object obj, String fieldName, Object filedValue) throws Exception {

        Field field = className.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, filedValue);
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

        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object returned = genericReflectionMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "doTransform");

        List<Specimen> list = (List<Specimen>) returned;

        String expectedId = "L.3355550@BRAHMS";
        String expectedGUID = "http://data.biodiversitydata.nl/naturalis/specimen/L.3355550";

        assertNotNull(list);
        assertTrue(list.size() == 1);
        assertEquals(expectedId, list.stream().map(i -> i.getId()).findFirst().get());
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

        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = genericReflectionMethod(record, CSVRecordInfo.class, brahmsSpecimenTransformer, "getGatheringEvent");
        GatheringEvent ge = (GatheringEvent) obj;

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

        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);
        Object obj = genericReflectionMethod(record, CSVRecordInfo.class, brahmsSpecimenTransformer, "getSpecimenIdentification");
        SpecimenIdentification identification = (SpecimenIdentification) obj;

        String expectedGenus = "Rhododendron";
        String expectedKingdom = "Plantae";
        String expectedSpecificEpithet = "ferrugineum";


        assertNotNull(identification);
        assertEquals(expectedGenus, identification.getDefaultClassification().getGenus());
        assertEquals(expectedSpecificEpithet, identification.getDefaultClassification().getSpecificEpithet());
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
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);

        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = genericReflectionMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getAssemblageID");
        String assembleId = (String) obj;

        String expectedAssembleID = "1993139@BRAHMS";

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
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Unknown ");
        when(record.get(BrahmsCsvField.PREFIX, false)).thenReturn(" ");
        when(record.get(BrahmsCsvField.NUMBER, false)).thenReturn("s.n. ");
        when(record.get(BrahmsCsvField.SUFFIX, false)).thenReturn(" ");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);

        Object obj = genericReflectionMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getCollectorsFieldNumber");
        String actualCollectorsNumber = (String) obj;

        String expectedCollectionNumber = "Unknown  s.n. ";

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
        ETLStatistics etlStatistics = new ETLStatistics();

        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large");

        BrahmsSpecimenTransformer brahmsSpecimenTransformer = new BrahmsSpecimenTransformer(etlStatistics);
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "objectID", "L.3355550");
        setPrivateField(AbstractTransformer.class, brahmsSpecimenTransformer, "input", record);


        Object obj = genericReflectionMethod(null, CSVRecordInfo.class, brahmsSpecimenTransformer, "getServiceAccessPoints");

        List<ServiceAccessPoint> actualList = (List<ServiceAccessPoint>) obj;

        String expectedAccessPoint = "http://medialib.naturalis.nl/file/id/L.3355550/format/large";

        String actualAccessPoint = actualList.stream().map(i -> i.getAccessUri().toString()).findFirst().get();

        assertNotNull(actualList);
        assertEquals(expectedAccessPoint, actualAccessPoint);


    }



}
