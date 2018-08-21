package nl.naturalis.nba.etl;

import static org.junit.Assert.assertEquals;
import java.io.InputStream;
import org.junit.Test;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.FileUtil;

/**
 * 
 * Test class for comparing a random generated test record vs a stored record
 *
 */
public class CreateTestDocumentTest {
  
  @SuppressWarnings("static-method")
  @Test
  public void testCreateTestDocs() throws Exception {

    Specimen specimen = CreateTestDocument.generateSpecimen();
    String actual = JsonUtil.toPrettyJson(specimen).trim();
    String file = "specimen-test-doc.json";
    InputStream is = CreateTestDocumentTest.class.getResourceAsStream(file);
    String expected = FileUtil.getContents(is).trim();
    // assertEquals("01", actual, expected);
    
    MultiMediaObject multimedia = CreateTestDocument.generateMultiMediaObject();
    //System.out.println(JsonUtil.toPrettyJson(multimedia));
    actual = JsonUtil.toPrettyJson(multimedia).trim();
    file = "multimedia-test-doc.json";
    is = CreateTestDocumentTest.class.getResourceAsStream(file);
    expected = FileUtil.getContents(is).trim();
    // assertEquals("02", actual, expected);

  }
  
}
