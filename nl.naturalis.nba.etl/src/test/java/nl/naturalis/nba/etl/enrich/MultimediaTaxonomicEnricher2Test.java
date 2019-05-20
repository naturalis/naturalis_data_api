/**
 * 
 */
package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.col.CoLReferenceBatchImporter;
import nl.naturalis.nba.etl.col.CoLTaxonImporter;
import nl.naturalis.nba.etl.col.CoLVernacularNameBatchImporter;
import nl.naturalis.nba.etl.utils.DataMockUtil;
import nl.naturalis.nba.etl.utils.ETLDaoUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test Class for MultimediaTaxonomicEnricher2.java
 * 
 * @author Plabon
 *
 */
@SuppressWarnings({"unchecked"})
public class MultimediaTaxonomicEnricher2Test {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    // create and delete index for test..
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
    deleteIndex(DocumentType.MULTI_MEDIA_OBJECT);
    createIndex(DocumentType.MULTI_MEDIA_OBJECT);

    // Saving a test MultiMedia Object in ES..
    MultiMediaObject mockMmo = DataMockUtil.generateMultiMediaMockObj();
    ETLDaoUtil.saveMultiMediaObject(mockMmo, true);

    CoLTaxonImporter cti = new CoLTaxonImporter();
    String texa = AllTests.class.getResource("taxa.txt").getPath();
    cti.importCsv(texa);
    CoLVernacularNameBatchImporter cvbi = new CoLVernacularNameBatchImporter();
    String vernecular = AllTests.class.getResource("vernacular.txt").getPath();
    cvbi.importCsv(vernecular);
    CoLReferenceBatchImporter crbi = new CoLReferenceBatchImporter();
    String reference = AllTests.class.getResource("reference.txt").getPath();
    crbi.importCsv(reference);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.MultimediaTaxonomicEnricher2#enrichMultimedia(List<MultiMediaObject>
   * mmos)}.
   * 
   * Test to verify enrichMultimedia returns a an expected list of List<MultiMediaObject> mmos
   */
  @Test
  public void testEnrichMultimedia() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    MultiMediaContentIdentification mmci = new MultiMediaContentIdentification();
    mmci.setScientificName(scientificName);

    List<MultiMediaContentIdentification> list = new ArrayList<>();
    list.add(mmci);

    MultiMediaObject mmo = new MultiMediaObject();
    mmo.setId("L.1911711_2107143681@BRAHMS");
    mmo.setIdentifications(list);

    List<MultiMediaObject> mmoList = new ArrayList<>();
    mmoList.add(mmo);

    Object obj = ReflectionUtil.callStatic(MultimediaTaxonomicEnricher2.class, "enrichMultimedia",
        new Class[] {List.class}, new Object[] {mmoList});
    List<MultiMediaObject> actualList = (List<MultiMediaObject>) obj;
    MultiMediaObject actual = actualList.get(0);

    String expectedId = "L.1911711_2107143681@BRAHMS";
    String taxanomicStatus = "accepted name";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedGenus = "Larus";
    String expectedSpecificEpethet = "affinis";

    ScientificName actualName = actual.getIdentifications().get(0).getScientificName();

    assertEquals("01", expectedId, actual.getId());
    assertEquals("02", taxanomicStatus, actualName.getTaxonomicStatus().toString());
    assertEquals("03", expectedScientificNameGroup, actualName.getScientificNameGroup());
    assertEquals("04", expectedAuthorshipVerbatim, actualName.getAuthorshipVerbatim());
    assertEquals("05", expectedGenus, actualName.getGenusOrMonomial());
    assertEquals("06", expectedSpecificEpethet, actualName.getSpecificEpithet());
    assertEquals("07", expectedFullScientificName, actualName.getFullScientificName());

  }

}
