package nl.naturalis.nba.etl;

import org.junit.Test;

public class CreateTestDocumentTest {
  
  @SuppressWarnings("static-method")
  @Test
  public void testCreateTestDocs() throws Exception {
    CreateTestDocument test = new CreateTestDocument();
    test.createTestDocs(); 
  }

  @SuppressWarnings("static-method")
  @Test
  public void testDeleteTestDocs() {
    CreateTestDocument test = new CreateTestDocument();
    test.deleteTestDocs(); 
    
  }
  
}
