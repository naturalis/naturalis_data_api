package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.AUTHOR2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.GENUS;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.RANK1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.RANK2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP1;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SP2;
import static nl.naturalis.nba.etl.brahms.BrahmsCsvField.SPECIES;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.Monomial;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;;

/**
 * Test class for BrahmsImportUtil.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BrahmsImportUtil.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method","static-access"})
public class BrahmsImportUtilTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        String logFile = "log4j2.xml";
        URL logFileUrl = AllTests.class.getResource(logFile);
        String logFilePath = logFileUrl.getFile().toString();
        String dirPath = logFilePath.substring(0, logFilePath.lastIndexOf("/"));
        System.setProperty("nba.v2.conf.dir", dirPath);
        System.setProperty("brahms.data.dir", dirPath);
        System.setProperty("log4j.configurationFile", logFilePath);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

        BrahmsImportUtil.removeBackupExtension();// clears the backup extension after each run
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getCsvFiles()}.
     * 
     * Test for the method getCSV file to very if CSV files are read from the local system
     * 
     * @throws Exception
     */
    @Test
    public void testGetCsvFiles() throws Exception {

        BrahmsImportUtil brahmsImportUtil = new BrahmsImportUtil();
        File[] actualFiles = brahmsImportUtil.getCsvFiles();
        String file = Arrays.asList(actualFiles).stream().findFirst().get().getName();
        assertNotNull(file);
        assertEquals(".CSV", (file.substring(file.indexOf("."))));
    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#backup()}.
     * 
     * @throws Exception
     * 
     *         Test to verify backup() method is called.
     */

    @Test
    public void testBackup() throws Exception {

        BrahmsImportUtil brahmsImportUtilSpy = PowerMockito.spy(new BrahmsImportUtil());
        brahmsImportUtilSpy.backup();
        Mockito.verify(brahmsImportUtilSpy, Mockito.times(1)).backup();

    }

    /**
     * Test method for {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#removeBackupExtension()}.
     * 
     * @throws Exception
     * 
     *         Test to verify removeExtension() method is called.
     */
    @Test
    public void testRemoveBackupExtension() throws Exception {
        BrahmsImportUtil brahmsImportUtilSpy = PowerMockito.spy(new BrahmsImportUtil());
        brahmsImportUtilSpy.removeBackupExtension();
        Mockito.verify(brahmsImportUtilSpy, times(1)).removeBackupExtension();
    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getScientificName(nl.naturalis.nba.etl.CSVRecordInfo)}.
     * 
     * Unit test to get the verify the scientific name when getFullScientificName is not null
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetScientificName_01() {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("");

        ScientificName expected = new ScientificName();
        expected.setFullScientificName("Rhododendron ferrugineum L.");
        expected.setAuthorshipVerbatim("L.");
        expected.setGenusOrMonomial("Rhododendron");
        expected.setSpecificEpithet("ferrugineum");
        expected.setInfraspecificMarker("");
        expected.setInfraspecificEpithet("");

        ScientificName actual = BrahmsImportUtil.getScientificName(record);
        assertNotNull(actual);
        assertEquals(expected.getFullScientificName(), actual.getFullScientificName());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getScientificName(nl.naturalis.nba.etl.CSVRecordInfo)}.
     * 
     * Unit test to get the verify the scientific name when getFullScientificName is null
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetScientificName_02() {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

        when(record.get(SPECIES)).thenReturn(null);
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("");

        ScientificName expected = new ScientificName();
        expected.setFullScientificName("Rhododendron ferrugineum   (L.)");
        expected.setAuthorshipVerbatim("L.");
        expected.setGenusOrMonomial("Rhododendron");
        expected.setSpecificEpithet("ferrugineum");
        expected.setScientificNameGroup("rhododendron ferrugineum ");
        expected.setInfraspecificMarker("");
        expected.setInfraspecificEpithet("");

        ScientificName actual = BrahmsImportUtil.getScientificName(record);
        assertNotNull(actual);
        assertEquals(expected.getFullScientificName(), actual.getFullScientificName());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getDefaultClassification(nl.naturalis.nba.etl.CSVRecordInfo, nl.naturalis.nba.api.model.ScientificName)}.
     * 
     * Test to verify the getDefault Classification
     * 
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetDefaultClassification() {

        String expectedGenus = "Rhododendron";
        String expectedFamilyname = "Plantae";

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

        when(record.get(SPECIES)).thenReturn(null);
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("");

        ScientificName scientificName = new ScientificName();
        scientificName.setFullScientificName("Rhododendron ferrugineum   (L.)");
        scientificName.setAuthorshipVerbatim("L.");
        scientificName.setGenusOrMonomial("Rhododendron");
        scientificName.setSpecificEpithet("ferrugineum");
        scientificName.setScientificNameGroup("rhododendron ferrugineum ");
        scientificName.setInfraspecificMarker("");
        scientificName.setInfraspecificEpithet("");

        DefaultClassification classification = BrahmsImportUtil.getDefaultClassification(record, scientificName);
        assertNotNull(classification);
        assertEquals(expectedGenus, classification.getGenus());
        assertEquals(expectedFamilyname, classification.getKingdom());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getSystemClassification(nl.naturalis.nba.api.model.DefaultClassification)}.
     * 
     * Test to verify the testGetSystemClassification() returns the correct system classifications.
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetSystemClassification() {

        List<Monomial> expectedList = new ArrayList<>();
        Monomial monomial_1 = new Monomial();
        monomial_1.setName("Plantae");
        monomial_1.setRank("kingdom");
        expectedList.add(monomial_1);
        Monomial monomial_2 = new Monomial();
        monomial_2.setName("Rhododendron");
        monomial_2.setRank("genus");
        expectedList.add(monomial_2);
        Monomial monomial_3 = new Monomial();
        monomial_3.setName("ferrugineum");
        monomial_3.setRank("species");
        expectedList.add(monomial_3);
        Monomial monomial_4 = new Monomial();
        monomial_4.setName("");
        monomial_4.setRank("subspecies");
        expectedList.add(monomial_4);

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("");

        ScientificName scientificName = new ScientificName();
        scientificName.setFullScientificName("Rhododendron ferrugineum   (L.)");
        scientificName.setAuthorshipVerbatim("L.");
        scientificName.setGenusOrMonomial("Rhododendron");
        scientificName.setSpecificEpithet("ferrugineum");
        scientificName.setScientificNameGroup("rhododendron ferrugineum ");
        scientificName.setInfraspecificMarker("");
        scientificName.setInfraspecificEpithet("");

        DefaultClassification classification = BrahmsImportUtil.getDefaultClassification(record, scientificName);
        List<Monomial> list = BrahmsImportUtil.getSystemClassification(classification);
        Map<String, String> actualResult = list.stream().collect(Collectors.toMap(Monomial::getName, Monomial::getRank));
        Map<String, String> expectedResult = expectedList.stream().collect(Collectors.toMap(Monomial::getName, Monomial::getRank));

        assertNotNull(list);
        assertEquals(4, list.size());
        assertEquals(expectedResult, actualResult);
        assertArrayEquals(actualResult.keySet().toArray(), expectedResult.keySet().toArray());
        assertArrayEquals(actualResult.values().toArray(), expectedResult.values().toArray());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getAuthor(nl.naturalis.nba.etl.CSVRecordInfo)}.
     * 
     * @throws Exception
     * 
     *         Test to check the private getAuthor() returns the correct author.
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetAuthor() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        ScientificName expected = new ScientificName();

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("");

        BrahmsImportUtil brahmsImportUtilSpy = PowerMockito.spy(new BrahmsImportUtil());
        PowerMockito.when(brahmsImportUtilSpy, "getAuthor", record).thenReturn("L.");
        expected = BrahmsImportUtil.getScientificName(record);
        expected.getAuthorshipVerbatim();

        assertNotNull(expected);
        assertEquals("L.", expected.getAuthorshipVerbatim());

    }


    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getAuthor(nl.naturalis.nba.etl.CSVRecordInfo)}.
     * 
     * @throws Exception
     * 
     *         Test to check the private getInfraspecificMarker() returns the correct
     *         InfraspecificMarker.
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetInfraspecificMarker() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        ScientificName expected = new ScientificName();

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("testInfraSpecifc");
        when(record.get(SP2)).thenReturn("");

        BrahmsImportUtil brahmsImportUtilSpy = PowerMockito.spy(new BrahmsImportUtil());
        PowerMockito.when(brahmsImportUtilSpy, "getInfraspecificMarker", record).thenReturn("testInfraSpecifc");
        expected = BrahmsImportUtil.getScientificName(record);
        assertNotNull(expected);
        assertEquals("testInfraSpecifc", expected.getInfraspecificMarker());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getAuthor(nl.naturalis.nba.etl.CSVRecordInfo)}.
     * 
     * @throws Exception
     * 
     *         Test to check the private getInfraspecificMarker() returns the correct
     *         InfraspecificMarker.
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetInfraspecificEpithet() throws Exception {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
        ScientificName expected = new ScientificName();

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("");
        when(record.get(SP2)).thenReturn("testInfraEpithat");

        BrahmsImportUtil brahmsImportUtilSpy = PowerMockito.spy(new BrahmsImportUtil());
        PowerMockito.when(brahmsImportUtilSpy, "getInfraspecificEpithet", record).thenReturn("testInfraEpithat");
        expected = BrahmsImportUtil.getScientificName(record);
        assertNotNull(expected);
        assertEquals("testInfraEpithat", expected.getInfraspecificEpithet());

    }

    /**
     * Test method for
     * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getTaxonRank(nl.naturalis.nba.etl.CSVRecordInfo)}.
     */
    @PrepareForTest(CSVRecordInfo.class)
    @Test
    public void testGetTaxonRank() {

        CSVRecordInfo<BrahmsCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

        when(record.get(SPECIES)).thenReturn("Rhododendron ferrugineum L.");
        when(record.get(AUTHOR2)).thenReturn("L.");
        when(record.get(GENUS)).thenReturn("Rhododendron");
        when(record.get(SP1)).thenReturn("ferrugineum");
        when(record.get(RANK2)).thenReturn("testInfraSpecifc");
        when(record.get(RANK1)).thenReturn("testTaxonRank");
        when(record.get(SP2)).thenReturn("testInfraEpithat");

        String actual = BrahmsImportUtil.getTaxonRank(record);
        assertEquals("testTaxonRank", actual);

    }


}
