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
import nl.naturalis.nba.etl.CSVRecordInfo;;

/**
 * Test class for BrahmsImportUtil.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BrahmsImportUtil.class,CSVRecordInfo.class})
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method", "static-access"})
public class BrahmsImportUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    BrahmsImportUtil.removeBackupExtension();// clears the backup extension after each run
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
   * Test for the method getCSV files to verify CSV files are read from the local system
   * 
   * @throws Exception
   */
  @Test
  public void testGetCsvFiles() throws Exception {

    BrahmsImportUtil brahmsImportUtil = new BrahmsImportUtil();
    File[] actualFiles = brahmsImportUtil.getCsvFiles();
    String file = Arrays.asList(actualFiles).stream().findFirst().get().getName();
    assertNotNull("01",file);
    assertEquals("02",".CSV", (file.substring(file.indexOf("."))));
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

    PowerMockito.mockStatic(BrahmsImportUtil.class);
    BrahmsImportUtil.backup();
    Mockito.verify(BrahmsImportUtil.class, Mockito.times(1));
    BrahmsImportUtil.backup();

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
    PowerMockito.mockStatic(BrahmsImportUtil.class);
    BrahmsImportUtil.removeBackupExtension();
    Mockito.verify(BrahmsImportUtil.class, times(1));
    BrahmsImportUtil.removeBackupExtension();
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getScientificName(nl.naturalis.nba.etl.CSVRecordInfo)}.
   * 
   * Test to verify getScientificName method returns the expected {@link ScientificName} object
   */
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
    assertNotNull("01",actual);
    assertEquals("02",expected.getFullScientificName(), actual.getFullScientificName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getScientificName(nl.naturalis.nba.etl.CSVRecordInfo)}.
   * 
   * Unit test to get the verify the {@link ScientificName} object when SPECIES field is null
   */
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
    assertNotNull("01",actual);
    assertEquals("02",expected.getFullScientificName(), actual.getFullScientificName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getDefaultClassification(nl.naturalis.nba.etl.CSVRecordInfo, nl.naturalis.nba.api.model.ScientificName)}.
   * 
   * Test to verify the getDefault Classification to see if the expcted
   * {@link DefaultClassification} object is returned
   * 
   */
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

    DefaultClassification classification =
        BrahmsImportUtil.getDefaultClassification(record, scientificName);
    assertNotNull("01",classification);
    assertEquals("02",expectedGenus, classification.getGenus());
    assertEquals("03",expectedFamilyname, classification.getKingdom());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getSystemClassification(nl.naturalis.nba.api.model.DefaultClassification)}.
   * 
   * Test to verify the testGetSystemClassification() returns the expected List<@link Monomial}>
   */
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

    DefaultClassification classification =
        BrahmsImportUtil.getDefaultClassification(record, scientificName);
    List<Monomial> list = BrahmsImportUtil.getSystemClassification(classification);
    Map<String, String> actualResult =
        list.stream().collect(Collectors.toMap(Monomial::getName, Monomial::getRank));
    Map<String, String> expectedResult =
        expectedList.stream().collect(Collectors.toMap(Monomial::getName, Monomial::getRank));

    assertNotNull("01",list);
    assertEquals("02",4, list.size());
    assertEquals("03",expectedResult, actualResult);
    assertArrayEquals("04",actualResult.keySet().toArray(), expectedResult.keySet().toArray());
    assertArrayEquals("05", actualResult.values().toArray(), expectedResult.values().toArray());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getAuthor(nl.naturalis.nba.etl.CSVRecordInfo)}.
   * 
   * @throws Exception
   * 
   *         Test to check the private getAuthor() returns the correct author name
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

    assertNotNull("01",expected);
    assertEquals("02","L.", expected.getAuthorshipVerbatim());

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
    PowerMockito.when(brahmsImportUtilSpy, "getInfraspecificMarker", record)
        .thenReturn("testInfraSpecifc");
    expected = BrahmsImportUtil.getScientificName(record);
    assertNotNull("01",expected);
    assertEquals("02","testInfraSpecifc", expected.getInfraspecificMarker());

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
    PowerMockito.when(brahmsImportUtilSpy, "getInfraspecificEpithet", record)
        .thenReturn("testInfraEpithat");
    expected = BrahmsImportUtil.getScientificName(record);
    assertNotNull("01",expected);
    assertEquals("02","testInfraEpithat", expected.getInfraspecificEpithet());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.brahms.BrahmsImportUtil#getTaxonRank(nl.naturalis.nba.etl.CSVRecordInfo)}.
   * 
   * Test to verify if getTaxonRank returns an expected Taxon Rank
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
