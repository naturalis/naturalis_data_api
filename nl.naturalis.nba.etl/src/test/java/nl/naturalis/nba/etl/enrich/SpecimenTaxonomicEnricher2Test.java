package nl.naturalis.nba.etl.enrich;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.etl.AllTests;
import nl.naturalis.nba.etl.col.CoLTaxonImporter;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test Class for SpecimenTaxonomicEnricher2.java
 * 
 * @author plabon
 *
 */
@SuppressWarnings({"static-method", "unchecked"})
public class SpecimenTaxonomicEnricher2Test {

  /**
   * @throws java.lang.Exception
   */

  @Before
  public void setUp() throws Exception {

    CoLTaxonImporter cti = new CoLTaxonImporter();
    String texa = AllTests.class.getResource("taxa.txt").getPath();
    cti.importCsv(texa);
//    CoLVernacularNameBatchImporter cvbi = new CoLVernacularNameBatchImporter();
//    String vernecular = AllTests.class.getResource("vernacular.txt").getPath();
//    cvbi.importCsv(vernecular);
//    CoLReferenceBatchImporter crbi = new CoLReferenceBatchImporter();
//    String reference = AllTests.class.getResource("reference.txt").getPath();
//    crbi.importCsv(reference);

  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for {@link nl.naturalis.nba.etl.enrich.SpecimenTaxonomicEnricher2#enrich()}.
   * 
   * Test method to verify enrichSpecimens method returns an expected List<Specimen> object
   */
  @Test
  public void testEnrichSpecimens() {

    ScientificName scientificName = new ScientificName();
    scientificName.setFullScientificName("Bombus affinis Cresson, 1863");
    scientificName.setScientificNameGroup("bombus affinis");
    scientificName.setAuthorshipVerbatim("Cresson, 1863");
    scientificName.setGenusOrMonomial("Larus");
    scientificName.setSpecificEpithet("affinis");
    scientificName.setInfraspecificEpithet("");
    scientificName.setTaxonomicStatus(TaxonomicStatus.ACCEPTED_NAME);

    SpecimenIdentification identification = new SpecimenIdentification();
    identification.setScientificName(scientificName);

    Specimen specimen = new Specimen();
    specimen.addIndentification(identification);

    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);

    Object obj = ReflectionUtil.callStatic(SpecimenTaxonomicEnricher2.class, "enrichSpecimens",
        new Class[] {List.class}, new Object[] {specimens});

    List<Specimen> expectedSpecimens = (List<Specimen>) obj;
    Specimen actual = expectedSpecimens.get(0);

    String taxanomicStatus = "accepted name";
    String expectedScientificNameGroup = "bombus affinis";
    String expectedAuthorshipVerbatim = "Cresson, 1863";
    String expectedFullScientificName = "Bombus affinis Cresson, 1863";
    String expectedGenus = "Larus";
    String expectedSpecificEpethet = "affinis";

    ScientificName name = actual.getIdentifications().get(0).getScientificName();

    assertEquals("01", taxanomicStatus, name.getTaxonomicStatus().toString());
    assertEquals("02", expectedScientificNameGroup, name.getScientificNameGroup());
    assertEquals("03", expectedAuthorshipVerbatim, name.getAuthorshipVerbatim());
    assertEquals("04", expectedFullScientificName, name.getFullScientificName());
    assertEquals("05", expectedGenus, name.getGenusOrMonomial());
    assertEquals("06", expectedSpecificEpethet, name.getSpecificEpithet());
  }
}
