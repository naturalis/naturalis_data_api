package nl.naturalis.nba.etl.col;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CoLSynonymBatchTransformer.java
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CSVRecordInfo.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method", "unchecked"})
public class CoLSynonymBatchTransformerTest {

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

    // First import a test data row into the ES store .
    CoLTaxonImporter cti = new CoLTaxonImporter();
    cti.importCsv(dirPath + "/taxa.txt");

    System.setProperty("nl.naturalis.nba.etl.testGenera",
        "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLSynonymBatchTransformer#transform(java.util.ArrayList)}.
   * 
   * Test to verify the transform method returns the correct {Collection<@Taxon> } object Since this
   * method calls the ES so the data needs to be present in the ES store (which are loading into the
   * ES in the setUp()
   * 
   * @throws Exception
   */
  @Test
  public void testTransform() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    ArrayList<CSVRecordInfo<CoLTaxonCsvField>> arrayList = new ArrayList<>();
    arrayList.add(record);
    CoLSynonymBatchTransformer coLTaxonTransformer = new CoLSynonymBatchTransformer();

    Object returned = ReflectionUtil.call(coLTaxonTransformer, "transform",
        new Class[] {ArrayList.class}, new Object[] {arrayList});
    Collection<Taxon> updates = (Collection<Taxon>) returned;
    Taxon actual = updates.iterator().next();

    String expectedId = "6931870@COL";
    String expectedRecordURI =
        "http://www.catalogueoflife.org/annual-checklist/2016/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedSourceSystemName = "Species 2000 - Catalogue Of Life";
    String expectedSpcificEpithet = "affinis";

    assertNotNull(actual);
    assertEquals(expectedId, actual.getId());
    assertEquals(expectedRecordURI, actual.getRecordURI().toString());
    assertEquals(expectedAuthorshipVerbatim, actual.getValidName().getAuthorshipVerbatim());
    assertEquals(expectedScientificNameGroup, actual.getValidName().getScientificNameGroup());
    assertEquals(expectedFullScientificName, actual.getValidName().getFullScientificName());
    assertEquals(expectedSourceSystemName, actual.getSourceSystem().getName());
    assertEquals(expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLSynonymBatchTransformer#createLookupTable(ArrayList<CSVRecordInfo<CoLTaxonCsvField>>
   * records)}.
   * 
   * Test to verify createLookupTable method returns a correct {HashMap<String, @Taxon>} object.
   * 
   * Since this method calls the ES so the data needs to be present in the ES store (which are
   * loaded into the ES in the setUp()
   * 
   * @throws Exception
   */
  @Test
  public void testCreateLookupTable() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    List<CSVRecordInfo<CoLTaxonCsvField>> arrayList = new ArrayList<>();
    arrayList.add(record);

    Object returned = ReflectionUtil.callStatic(CoLSynonymBatchTransformer.class,
        "createLookupTable", new Class[] {ArrayList.class}, new Object[] {arrayList});
    HashMap<String, Taxon> actualResults = (HashMap<String, Taxon>) returned;

    String expectedKey = "6931870";
    Taxon actual = actualResults.get(expectedKey);
    // Collection<Taxon> updates = (Collection<Taxon>)returned;
    String expectedId = "6931870@COL";
    String expectedRecordURI =
        "http://www.catalogueoflife.org/annual-checklist/2016/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedSourceSystemName = "Species 2000 - Catalogue Of Life";
    String expectedSpcificEpithet = "affinis";

    assertEquals(expectedKey, actualResults.keySet().iterator().next());
    assertEquals(expectedId, actual.getId());
    assertEquals(expectedRecordURI, actual.getRecordURI().toString());
    assertEquals(expectedFullScientificName, actual.getAcceptedName().getFullScientificName());
    assertEquals(expectedScientificNameGroup, actual.getAcceptedName().getScientificNameGroup());
    assertEquals(expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());
    assertEquals(expectedSourceSystemName, actual.getSourceSystem().getName());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLSynonymBatchTransformer#createSynonym(ArrayList<CSVRecordInfo<CoLTaxonCsvField>>
   * records)}.
   * 
   * Test to verify createSynonym method returns a correct ScientificName object.
   * 
   * 
   * @throws Exception
   */
  @Test
  public void testCreateSynonym() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    Object returned = ReflectionUtil.callStatic(CoLSynonymBatchTransformer.class, "createSynonym",
        new Class[] {CSVRecordInfo.class}, new Object[] {record});
    ScientificName actual = (ScientificName) returned;

    String expectedGenus = "Bombus";
    String expectedScientificNameGroup = "bombus affinis ";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";

    assertNotNull(actual);
    assertEquals(expectedFullScientificName, actual.getFullScientificName());
    assertEquals(expectedScientificNameGroup, actual.getScientificNameGroup());
    assertEquals(expectedGenus, actual.getGenusOrMonomial());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.col.CoLSynonymBatchTransformer#hasSynonym(Taxon
   * taxon, ScientificName scientificName}.
   * 
   * Test to verify hasSynonym method returns a correct boolean value.
   *
   * 
   * @throws Exception
   */
  @Test
  public void testHasSynonym() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("6931870");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    ArrayList<CSVRecordInfo<CoLTaxonCsvField>> arrayList = new ArrayList<>();
    arrayList.add(record);

    Object retLookup = ReflectionUtil.callStatic(CoLSynonymBatchTransformer.class,
        "createLookupTable", new Class[] {ArrayList.class}, new Object[] {arrayList});
    HashMap<String, Taxon> actualTaxonMap = (HashMap<String, Taxon>) retLookup;

    Taxon taxon = actualTaxonMap.get("6931870");

    Object retScientificName = ReflectionUtil.callStatic(CoLSynonymBatchTransformer.class,
        "createSynonym", new Class[] {CSVRecordInfo.class}, new Object[] {record});
    ScientificName actual = (ScientificName) retScientificName;

    taxon.addSynonym(actual);

    Object returned = ReflectionUtil.callStatic(CoLSynonymBatchTransformer.class, "hasSynonym",
        new Class[] {Taxon.class, ScientificName.class}, new Object[] {taxon, actual});
    boolean hasSyn = (boolean) returned;

    assertTrue(hasSyn);

  }

}