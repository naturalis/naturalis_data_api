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

import nl.naturalis.nba.api.model.Reference;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test class for CoLReferenceBatchTransformer.java
 *
 */
@SuppressWarnings({"unchecked"})
public class CoLReferenceBatchTransformerTest {

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
   * {@link nl.naturalis.nba.etl.col.CoLReferenceBatchTransformer#transform(java.util.ArrayList)}.
   * 
   * Test to verify the transform method returns the correct {Collection<@Taxon> } object Since this
   * method calls the ES so the texa data needs to be present in the ES store (which are loading
   * into the ES in the setUp())
   */
  @Test
  public void testTransform() {

    CSVRecordInfo<CoLReferenceCsvField> record = mock(CSVRecordInfo.class);

    when(record.get(CoLReferenceCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLReferenceCsvField.creator)).thenReturn("Pfeiffer, L.");
    when(record.get(CoLReferenceCsvField.date)).thenReturn("1846");
    when(record.get(CoLReferenceCsvField.title)).thenReturn("Diagnosen einiger neuer Heliceen.");
    when(record.get(CoLReferenceCsvField.description)).thenReturn("Malakozoologische Blätter");
    when(record.get(CoLReferenceCsvField.identifier)).thenReturn("");
    when(record.get(CoLReferenceCsvField.type)).thenReturn("taxon");

    List<CSVRecordInfo<CoLReferenceCsvField>> list = new ArrayList<>();
    list.add(record);

    CoLReferenceBatchTransformer batchTransformer = new CoLReferenceBatchTransformer();

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
    String authorName = "Pfeiffer, L.";
    String citationDetail = "Malakozoologische Blätter";
    String titleCitation = "Diagnosen einiger neuer Heliceen.";
    String publicationDate = "1846-01-01T00:00Z";

    assertNotNull("01",actual);
    assertEquals("02",expectedId, actual.getId());
    assertNotNull("03", actual.getRecordURI());
    assertEquals("04",expectedAuthorshipVerbatim, actual.getValidName().getAuthorshipVerbatim());
    assertEquals("05",expectedScientificNameGroup, actual.getValidName().getScientificNameGroup());
    assertEquals("06",expectedFullScientificName, actual.getValidName().getFullScientificName());
    assertEquals("07",expectedSourceSystemName, actual.getSourceSystem().getName());
    assertEquals("08",expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());
    assertEquals("09",authorName, actual.getReferences().iterator().next().getAuthor().getFullName());
    assertEquals("10",citationDetail, actual.getReferences().iterator().next().getCitationDetail());
    assertEquals("11",titleCitation, actual.getReferences().iterator().next().getTitleCitation());
    assertEquals("12",publicationDate,
        actual.getReferences().iterator().next().getPublicationDate().toString());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformer#createLookupTable(CSVRecordInfo<CoLReferenceCsvField>
   * csvRecordInfo }.
   * 
   * Test to verify createLookupTable method returns a correct {HashMap<String, @Taxon>} object.
   * 
   * Since this method calls the ES so the data needs to be present in the ES store (which are
   * loaded into the ES in the setUp()
   */
  @Test
  public void testCreateLookupTable() {

    CSVRecordInfo<CoLReferenceCsvField> record = mock(CSVRecordInfo.class);

    when(record.get(CoLReferenceCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLReferenceCsvField.creator)).thenReturn("Pfeiffer, L.");
    when(record.get(CoLReferenceCsvField.date)).thenReturn("1846");
    when(record.get(CoLReferenceCsvField.title)).thenReturn("Diagnosen einiger neuer Heliceen.");
    when(record.get(CoLReferenceCsvField.description)).thenReturn("Malakozoologische Blätter");
    when(record.get(CoLReferenceCsvField.identifier)).thenReturn("");
    when(record.get(CoLReferenceCsvField.type)).thenReturn("taxon");

    List<CSVRecordInfo<CoLReferenceCsvField>> list = new ArrayList<>();
    list.add(record);

    Object returned = ReflectionUtil.callStatic(CoLReferenceBatchTransformer.class, "createLookupTable", new Class[] {ArrayList.class}, new Object[] {list});
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
    assertNotNull("03", actual.getRecordURI());
    assertEquals("04",expectedFullScientificName, actual.getAcceptedName().getFullScientificName());
    assertEquals("05",expectedScientificNameGroup, actual.getAcceptedName().getScientificNameGroup());
    assertEquals("06",expectedSpcificEpithet, actual.getDefaultClassification().getSpecificEpithet());
    assertEquals("07",expectedSourceSystemName, actual.getSourceSystem().getName());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.col.CoLVernacularNameBatchTransformer#createReference(CSVRecordInfo<CoLReferenceCsvField>
   * csvRecordInfo }.
   * 
   * Test to verify createSynonym method returns a correct {@Reference} object.
   */
  @Test
  public void testCreateReference() {

    CSVRecordInfo<CoLReferenceCsvField> record = mock(CSVRecordInfo.class);

    when(record.get(CoLReferenceCsvField.taxonID)).thenReturn("6931870");
    when(record.get(CoLReferenceCsvField.creator)).thenReturn("Pfeiffer, L.");
    when(record.get(CoLReferenceCsvField.date)).thenReturn("1846");
    when(record.get(CoLReferenceCsvField.title)).thenReturn("Diagnosen einiger neuer Heliceen.");
    when(record.get(CoLReferenceCsvField.description)).thenReturn("Malakozoologische Blätter");
    when(record.get(CoLReferenceCsvField.identifier)).thenReturn("");
    when(record.get(CoLReferenceCsvField.type)).thenReturn("taxon");

    Object returned = ReflectionUtil.callStatic(CoLReferenceBatchTransformer.class,
        "createReference", new Class[] {CSVRecordInfo.class}, new Object[] {record});
    Reference actual = (Reference) returned;

    String authorName = "Pfeiffer, L.";
    String citationDetail = "Malakozoologische Blätter";
    String titleCitation = "Diagnosen einiger neuer Heliceen.";
    String publicationDate = "1846-01-01T00:00Z";

    assertNotNull("01",actual);
    assertEquals("02",authorName, actual.getAuthor().getFullName());
    assertEquals("03",citationDetail, actual.getCitationDetail());
    assertEquals("04",titleCitation, actual.getTitleCitation());
    assertEquals("05",publicationDate, actual.getPublicationDate().toString());

  }

}
