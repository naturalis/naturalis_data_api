package nl.naturalis.nba.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;

public class DownloadTest {

  private String baseUrl = "http://localhost:8080/v2";
  private SpecimenClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDownAfterClass() throws Exception {}

  @Before
  public void setUp() throws Exception {
    ClientConfig config = new ClientConfig();
    config.setBaseUrl(baseUrl);
    config.setPreferGET(true);
    NbaSession session = new NbaSession(config);
    client = session.getSpecimenClient();
  }

  @After
  public void tearDown() throws Exception {}

  /*
   * Tests of the download services
   */
  @Test
  public void test_download() throws InvalidQueryException, IOException 
  {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("collectionType", "=", "Pisces"));
    qs.addCondition(new QueryCondition("gatheringEvent.country", "=", "Nederland"));
    
//    client.downloadQuery(qs, new DevNullOutputStream());
    OutputStream out = null;
    GZIPOutputStream gzipOut = null;
    try {
      out = new FileOutputStream("/var/tmp/test.gz");
      gzipOut = new GZIPOutputStream(out);
      client.downloadQuery(qs, gzipOut);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (gzipOut != null)
        gzipOut.close();
      if (out != null)
        out.close();
      }
    File f = new File("/var/tmp/test.gz");
    Assert.assertTrue(f.exists());
  }  
  
  
}
