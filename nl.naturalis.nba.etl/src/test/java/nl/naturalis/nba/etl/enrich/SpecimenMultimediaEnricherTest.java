package nl.naturalis.nba.etl.enrich;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.ScientificName;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.SpecimenIdentification;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.etl.utils.DataMockUtil;
import nl.naturalis.nba.etl.utils.ETLDaoUtil;
import nl.naturalis.nba.utils.reflect.ReflectionUtil;

/**
 * Test Class for SpecimenMultimediaEnricher.java
 * 
 * NOTE: this test has lost its usage: multimedia enrichment of CRS specimens
 * is done during Specimen Transformation.
 * 
 * @see {@link nl.naturalis.nba.etl.crs.CrsSpecimenTransformer#getAssociatedMultiMediaUris()}
 *
 */
@SuppressWarnings({"unchecked"})
public class SpecimenMultimediaEnricherTest {
  
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    
    // Prepare the multimedia_integration_test index
    deleteIndex(DocumentType.MULTI_MEDIA_OBJECT);
    createIndex(DocumentType.MULTI_MEDIA_OBJECT);    
    MultiMediaObject mockMmo = DataMockUtil.generateMultiMediaMockObj();
    ETLDaoUtil.saveMultiMediaObject(mockMmo, true);    
  }

  /**
   * @throws java.lang.Exception
   */
  @After
  public void tearDown() throws Exception {}

  /**
   * Test method for
   * {@link nl.naturalis.nba.etl.enrich.SpecimenMultimediaEnricher#enrichSpecimens(List<Specimen>)}.
   * 
   * Test method to verify enrichSpecimens method returns an expected List<Specimen> object
   */
  @Test
  public void enrichSpecimens() {

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
    specimen.setId("L.1911711@BRAHMS");
    specimen.addIndentification(identification);

    List<Specimen> specimens = new ArrayList<>();
    specimens.add(specimen);
    Object obj = ReflectionUtil.callStatic(
        SpecimenMultimediaEnricher.class, "enrichSpecimens", new Class[] {List.class}, new Object[] {specimens}
        );
    List<Specimen> enrichedSpecimens = (List<Specimen>) obj;
    
    Specimen actual = enrichedSpecimens.get(0);
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
