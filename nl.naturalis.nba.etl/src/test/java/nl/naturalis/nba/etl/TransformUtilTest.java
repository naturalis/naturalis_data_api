/**
 * 
 */
package nl.naturalis.nba.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import nl.naturalis.nba.api.model.MultiMediaContentIdentification;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.Taxon;

/**
 * Test class for Transformutil.java
 *
 */
@PrepareForTest(TransformUtil.class)
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@SuppressWarnings({"static-method"})
public class TransformUtilTest {

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
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#extractClassificiationFromName(nl.naturalis.nba.api.model.ScientificName)}.
   * 
   * Test to verify extractClassificiationFromName method returns an expected DefaultClassification
   * object
   */

  @Test
  public void testExtractClassificiationFromName() {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification actual = TransformUtil.extractClassificiationFromName(name);

    assertNotNull(actual);
    assertEquals("testSubGenus", actual.getSubgenus());
    assertEquals("argentatus", actual.getSpecificEpithet());
    assertEquals("Larus", actual.getGenus());
    assertEquals("argentatus", actual.getInfraspecificEpithet());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#getMonomialsInName(nl.naturalis.nba.api.model.ScientificName)}.
   * 
   * Test to verify if getMonomialsInName returns an {@link List<@Monomial>} object
   */
  @Test
  public void testGetMonomialsInName() {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    List<Monomial> actual = TransformUtil.getMonomialsInName(name);

    assertNotNull(actual);
    assertEquals("Larus", actual.get(0).getName());
    assertEquals("genus", actual.get(0).getRank());
    assertEquals("testSubGenus", actual.get(1).getName());
    assertEquals("subgenus", actual.get(1).getRank());
    assertEquals("argentatus", actual.get(2).getName());
    assertEquals("species", actual.get(2).getRank());
    assertEquals("argentatus", actual.get(3).getName());
    assertEquals("subspecies", actual.get(3).getRank());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#extractNameFromClassification(nl.naturalis.nba.api.model.DefaultClassification)}.
   * 
   * Test to verify if extractNameFromClassification method returns the expected
   * {@link DefaultClassification} object
   */
  @Test
  public void testExtractNameFromClassification() {

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("testSubGenus");
    classification.setSpecificEpithet("argentatus");
    classification.setInfraspecificEpithet("argentatus");
    classification.setGenus("Larus");

    ScientificName actual = TransformUtil.extractNameFromClassification(classification);

    assertNotNull(actual);
    assertEquals("testSubGenus", actual.getSubgenus());
    assertEquals("argentatus", actual.getSpecificEpithet());
    assertEquals("argentatus", actual.getInfraspecificEpithet());
    assertEquals("Larus", actual.getGenusOrMonomial());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#equalizeNameComponents(nl.naturalis.nba.api.model.Taxon)}.
   *
   * Test to verify if the equalizeNameComponents which takes in {@link Taxon} object is invoked or
   * not
   *
   * @throws NameMismatchException
   * 
   */

  @Test
  public void testEqualizeNameComponentsTaxon() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("testSubGenus");
    classification.setSpecificEpithet("argentatus");
    classification.setInfraspecificEpithet("argentatus");
    classification.setGenus("Larus");

    Taxon taxon = new Taxon();
    taxon.setAcceptedName(name);
    taxon.setDefaultClassification(classification);
    PowerMockito.mockStatic(TransformUtil.class);
    TransformUtil.equalizeNameComponents(taxon);
    PowerMockito.verifyStatic(TransformUtil.class, Mockito.times(1));
    TransformUtil.equalizeNameComponents(taxon);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#equalizeNameComponents(nl.naturalis.nba.api.model.Specimen)}.
   * 
   * Test to verify if the equalizeNameComponents which takes in {@link Specimen} object is invoked
   * or not
   * 
   * @throws NameMismatchException
   * 
   *
   */
  @Test
  public void testEqualizeNameComponentsSpecimen() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("testSubGenus");
    classification.setSpecificEpithet("argentatus");
    classification.setInfraspecificEpithet("argentatus");
    classification.setGenus("Larus");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setDefaultClassification(classification);
    identification.setScientificName(name);

    List<SpecimenIdentification> identifications = new ArrayList<>();
    identifications.add(identification);

    Specimen specimen = new Specimen();
    specimen.setIdentifications(identifications);;

    PowerMockito.mockStatic(TransformUtil.class);
    TransformUtil.equalizeNameComponents(specimen);
    PowerMockito.verifyStatic(TransformUtil.class, Mockito.times(1));
    TransformUtil.equalizeNameComponents(specimen);

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#equalizeNameComponents(nl.naturalis.nba.api.model.MultiMediaObject))}.
   * 
   * Test to verify if the equalizeNameComponents which takes in {@link MultiMediaObject} object is
   * invoked or not
   * 
   * @throws NameMismatchException
   */
  @Test
  public void testEqualizeNameComponentsMultiMediaObjects() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("testSubGenus");
    classification.setSpecificEpithet("argentatus");
    classification.setInfraspecificEpithet("argentatus");
    classification.setGenus("Larus");

    MultiMediaContentIdentification identification = new MultiMediaContentIdentification();
    identification.setDefaultClassification(classification);
    identification.setScientificName(name);

    List<MultiMediaContentIdentification> identifications = new ArrayList<>();
    identifications.add(identification);

    MultiMediaObject mmo = new MultiMediaObject();
    mmo.setIdentifications(identifications);

    PowerMockito.mockStatic(TransformUtil.class);
    TransformUtil.equalizeNameComponents(mmo);
    PowerMockito.verifyStatic(TransformUtil.class, Mockito.times(1));
    TransformUtil.equalizeNameComponents(mmo);

  }

  /**
   * Test method for {@link nl.naturalis.nba.etl.TransformUtil#guessMimeType(java.lang.String)}.
   * 
   * Test to verify guessMimeType returns the correct MimeType
   */
  @Test
  public void testGuessMimeType() {

    String expectedMimeType = "image/jpeg";
    String actual = TransformUtil.guessMimeType(
        "http://images.naturalis.nl/original/104527_zilvermeeuw-20130210-egmond_aan_zee-001arnold_wijker.jpg");

    assertNotNull(actual);
    assertEquals(expectedMimeType, actual);
  }

}
