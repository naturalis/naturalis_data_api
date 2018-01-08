package nl.naturalis.nba.etl.brahms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.net.URI;
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
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;

/**
 * Test class for BrahmsMultiMediaTransformer.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CSVRecordInfo.class, BrahmsMultiMediaTransformer.class})
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method", "unchecked"})
public class BrahmsMultiMediaTransformerTest {


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

    @After
    public void tearDown() throws Exception {}



    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#doTransform()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the do Transform object returns the correct {@List<MultiMediaObject>}
     *         object
     */

    @Test
    public void testDoTransform() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
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

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);

        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsMultiMediaTransformer, "doTransform");
        String expectedAssociatedSpecimenRef = "L.3355550@BRAHMS";
        String expectedSourceID = "Brahms";
        List<MultiMediaObject> list = (List<MultiMediaObject>) obj;

        assertNotNull(list);
        assertTrue(list.size() == 1);
        assertEquals(expectedAssociatedSpecimenRef, list.stream().map(i -> i.getAssociatedSpecimenReference()).findFirst().get());
        assertEquals(expectedSourceID, list.stream().map(i -> i.getSourceID()).findFirst().get());

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#transformOne()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the transform One object returns the correct {@MultiMediaObject} object
     */

    @Test
    public void testTransformOne() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

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
        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);

        String url = "http://medialib.naturalis.nl/file/id/L.3355550/format/large";

        String expectedAssociatedSpecimenRef = "L.3355550@BRAHMS";

        Object obj = CommonReflectionUtil.callMethod(url, String.class, brahmsMultiMediaTransformer, "transformOne");

        MultiMediaObject mediaObject = (MultiMediaObject) obj;
        assertNotNull(mediaObject);
        assertEquals(expectedAssociatedSpecimenRef, mediaObject.getAssociatedSpecimenReference());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#getIdentification()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the getIdentification() method returns the correct
     *         {@MultiMediaContentIdentification} object
     */

    @Test
    public void testGetIdentification() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

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
        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);

        String expectedScientificNameGroup = "rhododendron ferrugineum ";
        String expectedTaxonGroup = "L.";

        Object obj = CommonReflectionUtil.callMethod(null, CSVRecordInfo.class, brahmsMultiMediaTransformer, "getIdentification");

        MultiMediaContentIdentification contentIdentification = (MultiMediaContentIdentification) obj;

        assertNotNull(contentIdentification);
        assertEquals(expectedScientificNameGroup, contentIdentification.getScientificName().getScientificNameGroup());
        assertEquals(expectedTaxonGroup, contentIdentification.getScientificName().getAuthorshipVerbatim());
    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#getMultiMediaGatheringEvent()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the getMultiMediaGatheringEvent One object returns the correct
     *         {@MultiMediaGatheringEvent} object
     */

    @Test
    public void testGetMultiMediaGatheringEvent() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        when(record.get(BrahmsCsvField.CONTINENT)).thenReturn("Europe");
        when(record.get(BrahmsCsvField.COUNTRY)).thenReturn("Netherlands");
        when(record.get(BrahmsCsvField.MAJORAREA)).thenReturn("");
        when(record.get(BrahmsCsvField.LOCNOTES)).thenReturn("Io een rotsachtige rij vochtige berghelling bij het Kolmhans.");
        when(record.get(BrahmsCsvField.YEAR)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.MONTH)).thenReturn("      8.000000");
        when(record.get(BrahmsCsvField.DAY)).thenReturn("      3.000000");
        when(record.get(BrahmsCsvField.LATITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.LONGITUDE)).thenReturn("      0.000000");
        when(record.get(BrahmsCsvField.BRAHMS)).thenReturn("1993139.000000");
        when(record.get(BrahmsCsvField.COLLECTOR, false)).thenReturn("Plabon");

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);

        Object obj = CommonReflectionUtil.callMethod(record, CSVRecordInfo.class, brahmsMultiMediaTransformer, "getMultiMediaGatheringEvent");
        MultiMediaGatheringEvent multiMediaGatheringEvent = (MultiMediaGatheringEvent) obj;

        assertNotNull(multiMediaGatheringEvent);
        assertEquals("Europe", multiMediaGatheringEvent.getContinent());
        assertEquals("Netherlands", multiMediaGatheringEvent.getCountry());
    }


    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#getUri()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the getUri One object returns the correct {@URI} object
     */

    @Test
    public void testGetUri() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large/");

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);

        String url = "http://medialib.naturalis.nl/file/id/Test URL/L.3355550/format/large";
        String expectedPath = "/file/id/Test URL/L.3355550/format/large";
        String expectedHost = "medialib.naturalis.nl";

        Object obj = CommonReflectionUtil.callMethod(url, String.class, brahmsMultiMediaTransformer, "getUri");

        URI uri = (URI) obj;
        assertNotNull(uri);
        assertEquals(expectedPath, uri.getPath());
        assertEquals(expectedHost, uri.getHost());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformer#newServiceAccessPoint()}.
     * 
     * @throws Exception
     * 
     *         Test to verify the newServiceAccessPoint One object returns the correct
     *         {@ServiceAccessPoint} object
     */

    @Test
    public void testNewServiceAccessPoint() throws Exception {

        ETLStatistics etlStatistics = new ETLStatistics();
        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        when(record.get(BrahmsCsvField.IMAGELIST)).thenReturn("http://medialib.naturalis.nl/file/id/L.3355550/format/large/");

        BrahmsMultiMediaTransformer brahmsMultiMediaTransformer = new BrahmsMultiMediaTransformer(etlStatistics);
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "objectID", "L.3355550");
        CommonReflectionUtil.setField(AbstractTransformer.class, brahmsMultiMediaTransformer, "input", record);


        URI uri = new URI("http://medialib.naturalis.nl/file/id/L.3355550/format/large");

        Object obj = CommonReflectionUtil.callMethod(uri, URI.class, brahmsMultiMediaTransformer, "newServiceAccessPoint");

        ServiceAccessPoint serviceAccessPoint = (ServiceAccessPoint) obj;

        assertNotNull(obj);
        assertEquals(ServiceAccessPoint.class, serviceAccessPoint.getClass());
        assertEquals(uri, serviceAccessPoint.getAccessUri());

    }



}
