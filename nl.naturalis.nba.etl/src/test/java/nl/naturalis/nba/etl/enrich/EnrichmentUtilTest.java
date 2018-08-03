package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.ServiceAccessPoint;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.TaxonomicEnrichment;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.col.CoLTaxonImporter;
import nl.naturalis.nba.etl.utils.DataMockUtil;
import nl.naturalis.nba.etl.utils.ETLDaoUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for EnrichmentUtil.java
 * 
 * @author plabon
 * 
 *
 */
@SuppressWarnings({"static-method", "unchecked"})
public class EnrichmentUtilTest {

  String dirPath = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    // delete and create new index..
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
    deleteIndex(DocumentType.MULTI_MEDIA_OBJECT);
    createIndex(DocumentType.MULTI_MEDIA_OBJECT);

    // Saving a test MultiMedia Object in ES..
    MultiMediaObject mockMmo = DataMockUtil.generateMultiMediaMockObj();
    ETLDaoUtil.saveMultiMediaObject(mockMmo, true);

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
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#createTaxonLookupTableForSpecimens(java.util.List)}.
   * 
   * Test method to verify if createTaxonLookupTableForSpecimens returns an expected Map<String,
   * List<Taxon>> object The ES index of Taxon needs to be populated for this integration test to
   * pass.
   */

  @Test
  public void testCreateTaxonLookupTableForSpecimens() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);

    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);

    Map<String, List<Taxon>> actual = EnrichmentUtil.createTaxonLookupTableForSpecimens(specimens);
    String actualKeyValue = actual.entrySet().stream().map(i -> i.getKey()).findFirst().get();

    List<Taxon> taxonList = actual.get(actualKeyValue);

    String expectedKeyValue = "bombus affinis";
    String expectedId = "6931870@COL";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";

    assertEquals("01", expectedKeyValue, actualKeyValue);
    assertEquals("02", expectedId, taxonList.get(0).getId());
    assertEquals("03", expectedScientificNameGroup,
        taxonList.get(0).getAcceptedName().getScientificNameGroup());
    assertEquals("04", expectedFullScientificName,
        taxonList.get(0).getAcceptedName().getFullScientificName());
    assertEquals("05", expectedAuthorshipVerbatim,
        taxonList.get(0).getAcceptedName().getAuthorshipVerbatim());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#createMultiMediaLookupTableForSpecimens(java.util.List)}.
   * 
   * Test method to verify if createTaxonLookupTableForSpecimens returns an expected Map<String,
   * List<ServiceAccessPoint>> object
   */
  @Test
  public void testCreateMultiMediaLookupTableForSpecimens() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);
    specimen.setId("L.1911711@BRAHMS");
    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);

    Map<String, List<ServiceAccessPoint>> actual =
        EnrichmentUtil.createMultiMediaLookupTableForSpecimens(specimens);

    String expectedKey = "L.1911711@BRAHMS";
    String actualKey = actual.entrySet().stream().map(i -> i.getKey()).findFirst().get();
    String expectedUri = "http://medialib.naturalis.nl/file/id/L.1911711/format/large";
    String expectedFormat = "image/jpeg";

    List<ServiceAccessPoint> list = actual.get(actualKey);

    assertNotNull("01", list);
    assertEquals("02", expectedKey, actualKey);
    assertEquals("03", expectedUri, list.get(0).getAccessUri().toString());
    assertEquals("04", expectedFormat, list.get(0).getFormat());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#extractNamesFromSpecimens(List<@Specimen>}.
   * 
   * Test Methods to verify extractNamesFromSpecimens methods returns an array of expected full name
   * String(s)
   */

  @Test
  public void testExtractNamesFromSpecimens() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);

    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);

    Object actual = ReflectionUtil.callStatic(EnrichmentUtil.class, "extractNamesFromSpecimens",
        new Class[] {List.class}, new Object[] {specimens});
    String[] resultArray = (String[]) actual;

    String expectedName = "bombus affinis";
    assertNotNull("01", resultArray);
    assertEquals("02", expectedName, resultArray[0]);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#extractIdsFromSpecimens(List<@Specimen>)}.
   * 
   * Test Methods to verify testExtractIdsFromSpecimens methods returns the an array of expected Ids
   */

  @Test
  public void testExtractIdsFromSpecimens() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);

    Specimen specimen = new Specimen();
    specimen.setId("L.1911711@BRAHMS");
    specimen.addIndentification(identification);

    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);

    Object actual = ReflectionUtil.callStatic(EnrichmentUtil.class, "extractIdsFromSpecimens",
        new Class[] {List.class}, new Object[] {specimens});
    String[] resultArray = (String[]) actual;

    String expectedId = "L.1911711@BRAHMS";
    assertNotNull("01", resultArray);
    assertEquals("02", expectedId, resultArray[0]);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#createTaxonLookupTableForMultiMedia(java.util.List)}.
   */
  @Test
  public void testCreateTaxonLookupTableForMultiMedia() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
    mmci.setScientificName(scientificName);

    List<MultiMediaContentIdentification> list = new ArrayList<>();
    list.add(mmci);

    MultiMediaObject mmo = new MultiMediaObject();
    mmo.setIdentifications(list);

    List<MultiMediaObject> mmoList = new ArrayList<>();
    mmoList.add(mmo);
    Map<String, List<Taxon>> actual = EnrichmentUtil.createTaxonLookupTableForMultiMedia(mmoList);

    String actualKeyValue = actual.entrySet().stream().map(i -> i.getKey()).findFirst().get();

    List<Taxon> taxonList = actual.get(actualKeyValue);

    String expectedKeyValue = "bombus affinis";
    String expectedId = "6931870@COL";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";

    assertEquals("01", expectedKeyValue, actualKeyValue);
    assertEquals("02", expectedId, taxonList.get(0).getId());
    assertEquals("03", expectedScientificNameGroup,
        taxonList.get(0).getAcceptedName().getScientificNameGroup());
    assertEquals("04", expectedFullScientificName,
        taxonList.get(0).getAcceptedName().getFullScientificName());
    assertEquals("05", expectedAuthorshipVerbatim,
        taxonList.get(0).getAcceptedName().getAuthorshipVerbatim());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#extractNamesFromMultiMedia(List<@MultiMediaObject>}.
   * 
   * Test Methods to verify extractNamesFromMultiMedia methods returns an array of expected full
   * name String(s)
   */

  @Test
  public void testExtractNamesFromMultiMedia() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");

    MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
    mmci.setScientificName(scientificName);

    List<MultiMediaContentIdentification> list = new ArrayList<>();
    list.add(mmci);

    MultiMediaObject mmo = new MultiMediaObject();
    mmo.setIdentifications(list);

    List<MultiMediaObject> mmoList = new ArrayList<>();
    mmoList.add(mmo);

    Object actual = ReflectionUtil.callStatic(EnrichmentUtil.class, "extractNamesFromMultiMedia",
        new Class[] {List.class}, new Object[] {mmoList});
    String[] resultArray = (String[]) actual;

    String expectedName = "bombus affinis";
    assertNotNull("01", resultArray);
    assertEquals("01", expectedName, resultArray[0]);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#loadMultiMedia(String[] specimenIds)}.
   * 
   * Test Methods to verify loadMultiMedia methods returns an array of expected QueryResult<@
   * MultiMediaObject> object
   */

  @Test
  public void testLoadMultiMedia() {

    String[] names = {"L.1911711@BRAHMS"};

    Object actual = ReflectionUtil.callStatic(EnrichmentUtil.class, "loadMultiMedia",
        new Class[] {String[].class}, new Object[] {names});
    QueryResult<MultiMediaObject> result = (QueryResult<MultiMediaObject>) actual;
    QueryResultItem<MultiMediaObject> actualObj = result.get(0);

    String expectedId = "L.1911711_2107143681@BRAHMS";
    String expectedLicenseType = "Copyright";
    String expectedLicense = "CC0";
    String expectedCollectionType = "Botany";
    String expectedTitle = "RMNH.AVES";

    assertEquals("01", expectedId, actualObj.getItem().getId());
    assertEquals("02", expectedLicenseType, actualObj.getItem().getLicenseType().toString());
    assertEquals("03", expectedLicense, actualObj.getItem().getLicense().toString());
    assertEquals("03", expectedCollectionType, actualObj.getItem().getCollectionType());
    assertEquals("04", expectedTitle, actualObj.getItem().getTitle());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#loadTaxa(String[] names)}.
   * 
   * Test Methods to verify loadTaxa methods returns an array of expected QueryResult<@ Taxon>
   * object
   */

  @Test
  public void testLoadTaxa() {

    String[] names = {"bombus affinis"};

    Object actual = ReflectionUtil.callStatic(EnrichmentUtil.class, "loadTaxa",
        new Class[] {String[].class}, new Object[] {names});
    QueryResult<Taxon> result = (QueryResult<Taxon>) actual;
    QueryResultItem<Taxon> actualObj = result.get(0);
    Taxon taxon = actualObj.getItem();

    String expectedId = "6931870@COL";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";

    assertEquals("01", expectedId, taxon.getId());
    assertEquals("02", expectedScientificNameGroup,
        taxon.getAcceptedName().getScientificNameGroup());
    assertEquals("03", expectedFullScientificName, taxon.getAcceptedName().getFullScientificName());
    assertEquals("04", expectedAuthorshipVerbatim, taxon.getAcceptedName().getAuthorshipVerbatim());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#createEnrichments(java.util.List)}.
   * 
   * Test to verify createEnrichments method returns an expected List<TaxonomicEnrichment> objects
   */
  @Test
  public void testCreateEnrichments() {

    VernacularName name = new VernacularName();
    name.setName("testVernecularName");
    name.setLanguage("English");
    List<VernacularName> vernacularNames = new ArrayList<>();
    vernacularNames.add(name);

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    List<ScientificName> synomyms = new ArrayList<>();
    synomyms.add(scientificName);

    SourceSystem sourceSystem = SourceSystem.getInstance("COL", "Species 2000 - Catalogue Of Life");

    Taxon taxon = new Taxon();
    taxon.setVernacularNames(vernacularNames);
    taxon.setSynonyms(synomyms);
    taxon.setSourceSystem(sourceSystem);
    taxon.setId("6931870@COL");
    List<Taxon> taxonList = new ArrayList<>();
    taxonList.add(taxon);

    List<TaxonomicEnrichment> result = EnrichmentUtil.createEnrichments(taxonList);
    TaxonomicEnrichment actual = result.get(0);

    assertNotNull("01", actual);
    assertEquals("02", "COL", actual.getSourceSystem().getCode());
    assertEquals("03", "6931870@COL", actual.getTaxonId());
    assertEquals("04", "English", actual.getVernacularNames().get(0).getLanguage());
    assertEquals("05", "testVernecularName", actual.getVernacularNames().get(0).getName());
    assertEquals("06", "Bombus affinis Cresson, 1863",
        actual.getSynonyms().get(0).getFullScientificName());
    assertEquals("07", "Larus", actual.getSynonyms().get(0).getGenusOrMonomial());
    assertEquals("08", "affinis", actual.getSynonyms().get(0).getSpecificEpithet());
    assertEquals("09", "accepted name",
        actual.getSynonyms().get(0).getTaxonomicStatus().toString());
  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.enrich.EnrichmentUtil#createTempFile()}.
   * 
   * Test Methods to createTempFile method returns a File object
   * 
   * @throws IOException
   */

  @Test
  public void testCreateTempFile() throws IOException {

    File actual = EnrichmentUtil.createTempFile(dirPath);
    assertNotNull(actual);
  }

}
