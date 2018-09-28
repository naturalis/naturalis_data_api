package nl.naturalis.nba.etl;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.LogicalOperator.AND;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.MultiMediaObjectDao;
import nl.naturalis.nba.dao.MultiMediaObjectMetaDataDao;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.dao.SpecimenMetaDataDao;
import nl.naturalis.nba.etl.utils.ETLDaoUtil;

/**
 * 
 * Test class for comparing a random generated test record vs a stored record
 *
 */
public class CreateTestDocumentTest {
  
  private static final Logger logger = DaoRegistry.getInstance().getLogger(CreateTestDocument.class);

  @BeforeClass
  public static void before() throws Exception
  {
    logger.info("Starting tests");
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
  }

  @SuppressWarnings("static-method")
  @Test
  public void testCreateSpecimenTestDoc() throws Exception {
    
    logger.info(">>> Specimen test document");
    
    Specimen specimen = CreateTestDocument.generateSpecimen();
    String id = specimen.getId();
    String unitID = id.substring(0, id.length()-4);
    specimen.setId(null);
    ETLDaoUtil.saveObject(id, null, specimen, true);
   
    logger.info("Specimen test id: " + id);
    
    SpecimenMetaDataDao specimenMetaDataDao = new SpecimenMetaDataDao();
    String[] paths = specimenMetaDataDao.getPaths(false);

    QuerySpec qs = new QuerySpec();
    QueryCondition condition_id = new QueryCondition("unitID", EQUALS, unitID);
    qs.addCondition(condition_id);

    SpecimenDao dao = new SpecimenDao();
    assertEquals( "01", 1, dao.count(qs));
    assertTrue(   "02", dao.find(id) != null);
    
    for (String path : paths) {
      if (
          path.equals("unitGUID") ||
          path.equals("identifications.taxonomicEnrichments.taxonId") ||
          path.equals("associatedMultiMediaUris.accessUri") ||
          path.equals("gatheringEvent.siteCoordinates.geoShape")
         ) continue;  // unitGUID, identifications.taxonomicEnrichments.taxonId and associatedMultiMediaUris.accessUri are not indexed
                      // gatheringEvent.siteCoordinates.geoShape cannot be searched with EQUALS
      logger.info("> " + path);
      qs.addCondition(condition_id);
      qs.addCondition(new QueryCondition(path, NOT_EQUALS, null));
      qs.setLogicalOperator(AND);
      assertEquals("field: " + path, 1, dao.count(qs));
      qs = new QuerySpec();
    }
    dao.delete(id, true);
  }

  @SuppressWarnings("static-method")
  @Test
  public void testCreateMultiMediaObjectTestDoc() throws Exception {
    
    logger.info("MultiMediaObject test document");
    
    MultiMediaObject multimedia = CreateTestDocument.generateMultiMediaObject();
    String id = multimedia.getId();
    String unitID = id.substring(0, id.length()-4);
    multimedia.setId(null);
    ETLDaoUtil.saveObject(id, null, multimedia, true);
   
    logger.info("MultiMediaObject test id: " + id);
    
    MultiMediaObjectMetaDataDao multimediaMetaDataDao = new MultiMediaObjectMetaDataDao();
    String[] paths = multimediaMetaDataDao.getPaths(false);

    QuerySpec qs = new QuerySpec();
    QueryCondition condition_id = new QueryCondition("unitID", EQUALS, unitID);
    qs.addCondition(condition_id);

    MultiMediaObjectDao dao = new MultiMediaObjectDao();
    assertEquals( "01", 1, dao.count(qs));
    assertTrue(   "02", dao.find(id) != null);
    
    for (String path : paths) {
      if (
          path.equals("unitGUID") ||
          path.equals("serviceAccessPoints.accessUri") ||
          path.equals("gatheringEvents.siteCoordinates.geoShape") ||
          path.equals("identifications.taxonomicEnrichments.taxonId")
         ) continue;  // unitGUID, identifications.taxonomicEnrichments.taxonId and associatedMultiMediaUris.accessUri are not indexed
                      // gatheringEvent.siteCoordinates.geoShape cannot be searched with EQUALS
      logger.info("> " + path);
      qs.addCondition(condition_id);
      qs.addCondition(new QueryCondition(path, NOT_EQUALS, null));
      qs.setLogicalOperator(AND);
      assertEquals("field: " + path, 1, dao.count(qs));
      qs = new QuerySpec();
    }
    dao.delete(id, true);
  }

  
}
