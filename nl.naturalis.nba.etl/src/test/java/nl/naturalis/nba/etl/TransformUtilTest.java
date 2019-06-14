/**
 * 
 */
package nl.naturalis.nba.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
public class TransformUtilTest {

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
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
  @Ignore
  @Test
  public void testExtractClassificiationFromName() {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification actual = TransformUtil.extractClassificiationFromName(name);

    assertNotNull("01",actual);
    assertEquals("02","testSubGenus", actual.getSubgenus());
    assertEquals("03","argentatus", actual.getSpecificEpithet());
    assertEquals("04","Larus", actual.getGenus());
    assertEquals("05","argentatus", actual.getInfraspecificEpithet());

  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#getMonomialsInName(nl.naturalis.nba.api.model.ScientificName)}.
   * 
   * Test to verify if getMonomialsInName returns an {@link List<@Monomial>} object
   */
  @Ignore
  @Test
  public void testGetMonomialsInName() {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    List<Monomial> actual = TransformUtil.getMonomialsInName(name);

    assertNotNull(actual);
    assertEquals("01","Larus", actual.get(0).getName());
    assertEquals("02","genus", actual.get(0).getRank());
    assertEquals("03","testSubGenus", actual.get(1).getName());
    assertEquals("04","subgenus", actual.get(1).getRank());
    assertEquals("05","argentatus", actual.get(2).getName());
    assertEquals("06","species", actual.get(2).getRank());
    assertEquals("07","argentatus", actual.get(3).getName());
    assertEquals("08","subspecies", actual.get(3).getRank());
  }

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#extractNameFromClassification(nl.naturalis.nba.api.model.DefaultClassification)}.
   * 
   * Test to verify if extractNameFromClassification method returns the expected
   * {@link DefaultClassification} object
   */
  @Ignore
  @Test
  public void testExtractNameFromClassification() {

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("testSubGenus");
    classification.setSpecificEpithet("argentatus");
    classification.setInfraspecificEpithet("argentatus");
    classification.setGenus("Larus");

    ScientificName actual = TransformUtil.extractNameFromClassification(classification);

    assertNotNull(actual);
    assertEquals("01","testSubGenus", actual.getSubgenus());
    assertEquals("02","argentatus", actual.getSpecificEpithet());
    assertEquals("03","argentatus", actual.getInfraspecificEpithet());
    assertEquals("04","Larus", actual.getGenusOrMonomial());

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
  @Ignore
  @Test
  public void testEqualizeNameComponentsTaxon_01() throws NameMismatchException {

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
   * {@link nl.naturalis.nba.etl.TransformUtil#equalizeNameComponents(nl.naturalis.nba.api.model.Taxon)}.
   *
   * Test to verify if the equalizeNameComponents which takes in {@link Taxon} object throws a NameMismatchException if the DefaultClassification and ScientificName values are different 
   *
   * @throws NameMismatchException
   * 
   */
  @Ignore
  @Test(expected=NameMismatchException.class)
  public void testEqualizeNameComponentsTaxon_02() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("test");
    classification.setSpecificEpithet("randaomSpEp");
    classification.setInfraspecificEpithet("randomINSpEp");
    classification.setGenus("Rosa");

    
    Taxon taxon = new Taxon();
    taxon.setAcceptedName(name);
    taxon.setDefaultClassification(classification);

    TransformUtil.equalizeNameComponents(taxon);

  }

  
  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.TransformUtil#equalizeNameComponents(nl.naturalis.nba.api.model.Taxon)}.
   *
   * Test to verify if the equalizeNameComponents which takes in {@link Taxon} object throws NullPointerException if  DefaultClassification/ScientificName or both are null
   *
   * @throws NameMismatchException
   * 
   */
  @Ignore
  @Test(expected=NameMismatchException.class)
  public void testEqualizeNameComponentsTaxon_03() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    DefaultClassification classification = new DefaultClassification();
    classification.setSubgenus("test");
    classification.setSpecificEpithet("randaomSpEp");
    classification.setInfraspecificEpithet("randomINSpEp");
    classification.setGenus("Rosa");

    
    Taxon taxon = new Taxon();
    taxon.setAcceptedName(name);
    taxon.setDefaultClassification(classification);

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
  @Ignore
  @Test(expected=NullPointerException.class)
  public void testEqualizeNameComponentsSpecimen() throws NameMismatchException {

    ScientificName name = new ScientificName();
    name.setGenusOrMonomial("Larus");
    name.setSpecificEpithet("argentatus");
    name.setInfraspecificEpithet("argentatus");
    name.setSubgenus("testSubGenus");

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setDefaultClassification(null);
    identification.setScientificName(name);

    List<SpecimenIdentification> identifications = new ArrayList<>();
    identifications.add(identification);

    Specimen specimen = new Specimen();
    specimen.setIdentifications(identifications);;

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
  @Ignore
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
  @Ignore
  @Test
  public void testGuessMimeType() {

    String expectedMimeType = "image/jpeg";
    String actual = TransformUtil.guessMimeType(
        "http://images.naturalis.nl/original/104527_zilvermeeuw-20130210-egmond_aan_zee-001arnold_wijker.jpg");

    assertNotNull("01",actual);
    assertEquals("02",expectedMimeType, actual);
  }

}
