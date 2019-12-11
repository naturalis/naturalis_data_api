package nl.naturalis.nba.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;

/**
 * 
 * Unit tests for the download service
 * 
 * @author Tom Gilissen
 * 
 */
public class DownloadTest {

  @Rule
  public TemporaryFolder testFolder = new TemporaryFolder();

  private String baseUrl = "http://localhost:8080/v3";
  private SpecimenClient client;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {}

  @AfterClass
  public static void tearDown() throws Exception {}

  @Before
  public void setUp() throws Exception {
    ClientConfig config = new ClientConfig();
    config.setBaseUrl(baseUrl);
    config.setPreferGET(true);
    NbaSession session = new NbaSession(config);
    client = session.getSpecimenClient();
  }

  @After
  public void tearDownAfterClass() throws Exception {}

  /*
   * Test with a GZIPOutputStream
   */
  @Test
  public void test_download_gzip() throws InvalidQueryException, IOException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("collectionType", "=", "Pisces"));
    qs.addCondition(new QueryCondition("gatheringEvent.country", "=", "Nederland"));

    OutputStream out = null;
    GZIPOutputStream gzipOut = null;
    File tempFile = testFolder.newFile("test.gz");
    try {
      out = new FileOutputStream(tempFile);
      gzipOut = new GZIPOutputStream(out);
      client.downloadQuery(qs, gzipOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (gzipOut != null)
        gzipOut.close();
      if (out != null)
        out.close();
    }
    Assert.assertTrue(tempFile.exists() && tempFile.length() > 0);
  }

  /*
   * Test with a FileOutputStream
   */
  @Test
  public void test_download_plain() throws InvalidQueryException, IOException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("collectionType", "=", "Pisces"));
    qs.addCondition(new QueryCondition("gatheringEvent.country", "=", "Nederland"));

    OutputStream out = null;
    File tempFile = testFolder.newFile("test.json");
    try {
      out = new FileOutputStream(tempFile);
      client.downloadQuery(qs, out);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (out != null)
        out.close();
    }
    Assert.assertTrue(tempFile.exists() && tempFile.length() > 0);
  }

  /*
   * Test with a ByteArrayOutputStream and comparison of the result with a download using the
   * FileOutputStream
   */
  @Test
  public void test_download_byte_stream() throws InvalidQueryException, IOException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("collectionType", "=", "Pisces"));
    qs.addCondition(new QueryCondition("gatheringEvent.country", "=", "Nederland"));

    ByteArrayOutputStream byteOut = null;
    byte[] result;
    try {
      byteOut = new ByteArrayOutputStream();
      client.downloadQuery(qs, byteOut);
      result = byteOut.toByteArray();
    } finally {
      if (byteOut != null)
        byteOut.close();
    }

    OutputStream fileOut = null;
    File tempFile = testFolder.newFile("test.json");
    try {
      fileOut = new FileOutputStream(tempFile);
      client.downloadQuery(qs, fileOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (fileOut != null)
        fileOut.close();
    }

    byte[] benchmark;
    FileInputStream fileIn = null;
    try {
      fileIn = new FileInputStream(tempFile);
      benchmark = IOUtils.toByteArray(fileIn);
    } finally {
      if (fileIn != null)
        fileIn.close();
    }

    Assert.assertTrue(Arrays.equals(result, benchmark));
  }

  @Test(expected = NullPointerException.class)
  public void test_no_outputstream() throws InvalidQueryException, IOException {
    QuerySpec qs = new QuerySpec();
    qs.addCondition(new QueryCondition("collectionType", "=", "Pisces"));
    qs.addCondition(new QueryCondition("gatheringEvent.country", "=", "Nederland"));

    OutputStream out = null;
    try {
      client.downloadQuery(qs, out);
    } finally {
      if (out != null)
        out.close();
    }
  }

}
