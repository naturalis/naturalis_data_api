package nl.naturalis.nba.etl.col;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CoLVernacularNameBatchTransformer.java
 *
 */
@SuppressWarnings({"unchecked"})
public class CoLVernacularNameBatchTransformerTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // First import a test data row into the ES store .
    CoLTaxonImporter cti = new CoLTaxonImporter();
    String path = AllTests.class.getResource("taxa.txt").getPath();
    cti.importCsv(path);
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformer#transform(java.util.ArrayList)}.
   * 
   * Test to verify the transform method returns the correct {Collection<@Taxon> } object Since this
   * method calls the ES so the texa data needs to be present in the ES store (which are loading
   * into the ES in the setUp())
   */
  @Test
  public void testTransform() {

    CSVRecordInfo<CoLVernacularNameCsvField> csvRecordInfo = mock(CSVRecordInfo.class);
    when(csvRecordInfo.get(CoLVernacularNameCsvField.taxonID)).thenReturn("6931870");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.vernacularName)).thenReturn("pinhead spot");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.language)).thenReturn("English US");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.countryCode)).thenReturn("USA");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.locality)).thenReturn("");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.transliteration)).thenReturn("");

    List<CSVRecordInfo<CoLVernacularNameCsvField>> list = new ArrayList<>();
    list.add(csvRecordInfo);

    CoLVernacularNameBatchTransformer batchTransformer = new CoLVernacularNameBatchTransformer();

    Object returned = ReflectionUtil.call(batchTransformer, "transform",
        new Class[] {ArrayList.class}, new Object[] {list});
    Collection<Taxon> updates = (Collection<Taxon>) returned;
    Taxon actual = updates.iterator().next();

    String expectedId = "6931870@COL";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedSourceSystemName = "Species 2000 - Catalogue Of Life";
    String expectedSpcificEpithet = "affinis";
    String expectedVernecularName = "pinhead spot";
    String expectedLanguage = "English US";

    assertNotNull("01",actual);
    assertEquals("02",expectedId, actual.getId());
    assertNotNull("03",actual.getRecordURI());
    assertEquals("04",expectedAuthorshipVerbatim, actual.getValidName().getAuthorshipVerbatim());
    assertEquals("05",expectedScientificNameGroup, actual.getValidName().getScientificNameGroup());
    assertEquals("06",expectedFullScientificName, actual.getValidName().getFullScientificName());
    assertEquals("07",expectedSourceSystemName, actual.getSourceSystem().getName());
    assertEquals("08",expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());
    assertEquals("09",expectedVernecularName, actual.getVernacularNames().iterator().next().getName());
    assertEquals("10",expectedLanguage, actual.getVernacularNames().iterator().next().getLanguage());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformer#testCreateLookupTable(CSVRecordInfo<CoLVernacularNameCsvField>
   * csvRecordInfo }.
   * 
   * Test to verify createLookupTable method returns a correct {HashMap<String, @Taxon>} object.
   * 
   * Since this method calls the ES so the data needs to be present in the ES store (which are
   * loaded into the ES in the setUp()
   */
  @Test
  public void testCreateLookupTable() {

    CSVRecordInfo<CoLVernacularNameCsvField> csvRecordInfo = mock(CSVRecordInfo.class);
    when(csvRecordInfo.get(CoLVernacularNameCsvField.taxonID)).thenReturn("6931870");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.vernacularName)).thenReturn("pinhead spot");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.language)).thenReturn("English US");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.countryCode)).thenReturn("USA");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.locality)).thenReturn("");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.transliteration)).thenReturn("");

    List<CSVRecordInfo<CoLVernacularNameCsvField>> list = new ArrayList<>();
    list.add(csvRecordInfo);

    Object returned = ReflectionUtil.callStatic(CoLVernacularNameBatchTransformer.class,
        "createLookupTable", new Class[] {ArrayList.class}, new Object[] {list});
    HashMap<String, Taxon> actualResults = (HashMap<String, Taxon>) returned;

    String expectedKey = "6931870";
    Taxon actual = actualResults.get(expectedKey);
    String expectedId = "6931870@COL";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedSourceSystemName = "Species 2000 - Catalogue Of Life";
    String expectedSpcificEpithet = "affinis";

    assertEquals("01",expectedKey, actualResults.keySet().iterator().next());
    assertEquals("02",expectedId, actual.getId());
    assertNotNull("03",actual.getRecordURI());
    assertEquals("04",expectedFullScientificName, actual.getAcceptedName().getFullScientificName());
    assertEquals("05",expectedScientificNameGroup, actual.getAcceptedName().getScientificNameGroup());
    assertEquals("06",expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());
    assertEquals("07",expectedSourceSystemName, actual.getSourceSystem().getName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformer#createVernacularName(CSVRecordInfo<CoLVernacularNameCsvField>
   * csvRecordInfo }.
   * 
   * Test to verify createSynonym method returns a correct VernacularName object.
   * 
   */
  @Test
  public void testCreateVernacularName() {

    CSVRecordInfo<CoLVernacularNameCsvField> csvRecordInfo = mock(CSVRecordInfo.class);
    when(csvRecordInfo.get(CoLVernacularNameCsvField.taxonID)).thenReturn("6931870");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.vernacularName)).thenReturn("pinhead spot");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.language)).thenReturn("English US");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.countryCode)).thenReturn("USA");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.locality)).thenReturn("");
    when(csvRecordInfo.get(CoLVernacularNameCsvField.transliteration)).thenReturn("");

    Object returned = ReflectionUtil.callStatic(CoLVernacularNameBatchTransformer.class,
        "createVernacularName", new Class[] {CSVRecordInfo.class}, new Object[] {csvRecordInfo});
    VernacularName actual = (VernacularName) returned;

    String expectedVenecularName = "pinhead spot";
    String expectedLangauge = "English US";

    assertNotNull("01",actual);
    assertEquals("02",expectedVenecularName, actual.getName());
    assertEquals("03",expectedLangauge, actual.getLanguage());

  }

}