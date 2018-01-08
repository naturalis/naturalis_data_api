package nl.naturalis.nba.etl.col;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
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
import nl.naturalis.nba.api.model.DefaultClassification;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.etl.AbstractTransformer;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.utils.CommonReflectionUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CoLTaxonTransformer.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(CSVRecordInfo.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({"static-method", "unchecked"})
public class CoLTaxonTransformerTest {

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
    System.setProperty("nl.naturalis.nba.etl.testGenera",
        "malus,parus,larus,bombus,rhododendron,felix,tulipa,rosa,canis,passer,trientalis");

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.col.CoLTaxonTransformer#doTransform()}.
   * 
   * Test to verify the doTransform method returns the correct {List<@Taxon> } object
   * 
   * @throws Exception
   */
  @Test
  public void testDoTransform() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
    ETLStatistics etlStatistics = new ETLStatistics();

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931872");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("6931872");
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
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("test_infraspecificEpithet");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("test_namePublishedIn");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("test_nameAccordingTo");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("test_taxonConceptID");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    CoLTaxonTransformer coLTaxonTransformer = new CoLTaxonTransformer(etlStatistics);

    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "objectID",
        "6931872");
    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "input", record);
    Object returned =
        ReflectionUtil.call(coLTaxonTransformer, "doTransform", new Class[] {}, new Object[] {});

    List<Taxon> list = (List<Taxon>) returned;

    String expectedId = "6931872@COL";
    String expectedRecordURI =
        "http://www.catalogueoflife.org/annual-checklist/null/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedScientificNameGroup = "bombus affinis test_infraspecificepithet";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedGenusOrMonomial = "Bombus";
    String expectedSpcificEpithet = "affinis";
    //

    assertNotNull(list);
    assertTrue(list.size() == 1);
    assertEquals(expectedId, list.stream().map(i -> i.getId()).findFirst().get());
    assertEquals(expectedRecordURI,
        list.stream().map(i -> i.getRecordURI()).findFirst().get().toString());
    assertEquals(expectedAuthorshipVerbatim, list.stream()
        .map(i -> i.getAcceptedName().getAuthorshipVerbatim()).findFirst().get().toString());
    assertEquals(expectedFullScientificName, list.stream()
        .map(i -> i.getAcceptedName().getFullScientificName()).findFirst().get().toString());
    assertEquals(expectedScientificNameGroup, list.stream()
        .map(i -> i.getAcceptedName().getScientificNameGroup()).findFirst().get().toString());
    assertEquals(expectedGenusOrMonomial, list.stream()
        .map(i -> i.getAcceptedName().getGenusOrMonomial()).findFirst().get().toString());
    assertEquals(expectedSpcificEpithet, list.stream()
        .map(i -> i.getAcceptedName().getSpecificEpithet()).findFirst().get().toString());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.col.CoLTaxonTransformer#getClassification()}.
   * 
   * Test to verify the getClassification method returns the correct DefaultClassification object
   * 
   * @throws Exception
   */
  @Test
  public void testGetClassification() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
    ETLStatistics etlStatistics = new ETLStatistics();

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931872");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("test_acceptedNameUsageID");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.family)).thenReturn("Apidae");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("test_infraspecificEpithet");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("test_namePublishedIn");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("test_nameAccordingTo");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("test_taxonConceptID");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    CoLTaxonTransformer coLTaxonTransformer = new CoLTaxonTransformer(etlStatistics);

    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "objectID",
        "6931872");
    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "input", record);

    Object returned = ReflectionUtil.call(coLTaxonTransformer, "getClassification", new Class[] {},
        new Object[] {});

    DefaultClassification defaultClassification = (DefaultClassification) returned;

    DefaultClassification expected = new DefaultClassification();
    expected.setKingdom("Animalia");
    expected.setPhylum("Arthropoda");
    expected.setClassName("Insecta");
    expected.setSuperFamily("Apoidea");
    expected.setFamily("Apidae");
    expected.setGenus("Bombus");
    expected.setSubgenus("test_subgenus");
    expected.setSpecificEpithet("affinis");

    assertNotNull(returned);
    assertEquals(expected.getKingdom(), defaultClassification.getKingdom());
    assertEquals(expected.getPhylum(), defaultClassification.getPhylum());
    assertEquals(expected.getClassName(), defaultClassification.getClassName());
    assertEquals(expected.getSuperFamily(), defaultClassification.getSuperFamily());
    assertEquals(expected.getFamily(), defaultClassification.getFamily());
    assertEquals(expected.getGenus(), defaultClassification.getGenus());
    assertEquals(expected.getSubgenus(), defaultClassification.getSubgenus());
    assertEquals(expected.getSpecificEpithet(), defaultClassification.getSpecificEpithet());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.col.CoLTaxonTransformer#getScientificName()}.
   * 
   * Test to verify the getScientificName method returns the correct ScientificName object
   * 
   * @throws Exception
   */
  @Test
  public void testGetScientificName() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
    ETLStatistics etlStatistics = new ETLStatistics();

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931872");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("test_acceptedNameUsageID");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.family)).thenReturn("Apidae");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("test_infraspecificEpithet");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("test_namePublishedIn");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("test_nameAccordingTo");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("test_taxonConceptID");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    CoLTaxonTransformer coLTaxonTransformer = new CoLTaxonTransformer(etlStatistics);

    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "objectID",
        "6931872");
    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "input", record);

    Object returned = ReflectionUtil.call(coLTaxonTransformer, "getScientificName", new Class[] {},
        new Object[] {});

    ScientificName name = (ScientificName) returned;

    ScientificName expected = new ScientificName();
    expected.setFullScientificName("Bombus affinis Cresson, 1863");
    expected.setGenusOrMonomial("Bombus");
    expected.setSubgenus("test_subgenus");
    expected.setSpecificEpithet("affinis");
    expected.setInfraspecificEpithet("test_infraspecificEpithet");
    expected.setAuthorshipVerbatim("Cresson, 1863");
    expected.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);
    expected.setScientificNameGroup("bombus affinis test_infraspecificepithet");

    assertNotNull(returned);
    assertEquals(expected.getFullScientificName(), name.getFullScientificName());
    assertEquals(expected.getGenusOrMonomial(), name.getGenusOrMonomial());
    assertEquals(expected.getSubgenus(), name.getSubgenus());
    assertEquals(expected.getSpecificEpithet(), name.getSpecificEpithet());
    assertEquals(expected.getInfraspecificEpithet(), name.getInfraspecificEpithet());
    assertEquals(expected.getAuthorshipVerbatim(), name.getAuthorshipVerbatim());
    assertEquals(expected.getTaxonomicStatus(), name.getTaxonomicStatus());
    assertEquals(expected.getScientificNameGroup(), name.getScientificNameGroup());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.col.CoLTaxonTransformer#isTestSetGenus()}.
   * 
   * Test to verify the isTestSetGenus method returns the correct boolean value
   * 
   * @throws Exception
   */
  @Test
  public void testIsTestSetGenus() throws Exception {

    CSVRecordInfo<CoLTaxonCsvField> record = PowerMockito.mock(CSVRecordInfo.class);
    ETLStatistics etlStatistics = new ETLStatistics();

    when(record.get(CoLTaxonCsvField.taxonID)).thenReturn("6931872");
    when(record.get(CoLTaxonCsvField.identifier)).thenReturn(
        "urn:lsid:catalogueoflife.org:taxon:6703cbb5-e478-11e5-86e7-bc764e092680:col20150315");
    when(record.get(CoLTaxonCsvField.datasetID)).thenReturn("67");
    when(record.get(CoLTaxonCsvField.datasetName))
        .thenReturn("ITIS Bees in Species 2000 & ITIS Catalogue of Life: 23rd March 2016");
    when(record.get(CoLTaxonCsvField.acceptedNameUsageID)).thenReturn("test_acceptedNameUsageID");
    when(record.get(CoLTaxonCsvField.parentNameUsageID)).thenReturn("27000241");
    when(record.get(CoLTaxonCsvField.taxonomicStatus)).thenReturn("accepted name");
    when(record.get(CoLTaxonCsvField.taxonRank)).thenReturn("species");
    when(record.get(CoLTaxonCsvField.verbatimTaxonRank)).thenReturn("test_verbatimTaxonRank");
    when(record.get(CoLTaxonCsvField.scientificName)).thenReturn("Bombus affinis Cresson, 1863");
    when(record.get(CoLTaxonCsvField.kingdom)).thenReturn("Animalia");
    when(record.get(CoLTaxonCsvField.phylum)).thenReturn("Arthropoda");
    when(record.get(CoLTaxonCsvField.classRank)).thenReturn("Insecta");
    when(record.get(CoLTaxonCsvField.family)).thenReturn("Apidae");
    when(record.get(CoLTaxonCsvField.superfamily)).thenReturn("Apoidea");
    when(record.get(CoLTaxonCsvField.genericName)).thenReturn("Bombus");
    when(record.get(CoLTaxonCsvField.subgenus)).thenReturn("test_subgenus");
    when(record.get(CoLTaxonCsvField.specificEpithet)).thenReturn("affinis");
    when(record.get(CoLTaxonCsvField.infraspecificEpithet)).thenReturn("test_infraspecificEpithet");
    when(record.get(CoLTaxonCsvField.scientificNameAuthorship)).thenReturn("Cresson, 1863");
    when(record.get(CoLTaxonCsvField.source)).thenReturn("test_source");
    when(record.get(CoLTaxonCsvField.namePublishedIn)).thenReturn("test_namePublishedIn");
    when(record.get(CoLTaxonCsvField.nameAccordingTo)).thenReturn("test_nameAccordingTo");
    when(record.get(CoLTaxonCsvField.modified)).thenReturn("30-Sep-2008");
    when(record.get(CoLTaxonCsvField.description)).thenReturn("test_desc");
    when(record.get(CoLTaxonCsvField.taxonConceptID)).thenReturn("test_taxonConceptID");
    when(record.get(CoLTaxonCsvField.scientificNameID)).thenReturn("ITS-714782");
    when(record.get(CoLTaxonCsvField.references)).thenReturn(
        "http://www.catalogueoflife.org/annual-checklist/details/species/id/39ed89a52a61ef3a59eef66b9ce8ad7e");

    CoLTaxonTransformer coLTaxonTransformer = new CoLTaxonTransformer(etlStatistics);

    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "objectID",
        "6931872");
    CommonReflectionUtil.setField(AbstractTransformer.class, coLTaxonTransformer, "input", record);

    Object returned =
        ReflectionUtil.call(coLTaxonTransformer, "isTestSetGenus", new Class[] {}, new Object[] {});

    boolean isTestGenus = (boolean) returned;

    assertTrue(isTestGenus);
  }

}