package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.SpecimenMock;

public class SpecimenDaoTest_DwcaTest {

  private static final Logger logger = DaoRegistry.getInstance().getLogger(SpecimenDaoTest_DwcaTest.class);

  static Specimen pMajor;
  static Specimen lFuscus1;
  static Specimen lFuscus2;
  static Specimen tRex;
  static Specimen mSylvestris;

  @BeforeClass
  public static void before() {
    logger.info("Start");
    deleteIndex(DocumentType.SPECIMEN);
    createIndex(DocumentType.SPECIMEN);
    /*
     * Insert 5 test specimens.
     */
    pMajor = SpecimenMock.parusMajorSpecimen01();
    lFuscus1 = SpecimenMock.larusFuscusSpecimen01();
    lFuscus2 = SpecimenMock.larusFuscusSpecimen02();
    tRex = SpecimenMock.tRexSpecimen01();
    mSylvestris = SpecimenMock.malusSylvestrisSpecimen01();
    DaoTestUtil.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
  }

  @After
  public void after() {
    // dropIndex(Specimen.class);
  }

  @Ignore
  @Test
  public void test_dwcaGetDataSetNames() {
    SpecimenDao dao = new SpecimenDao();
    assertTrue("01 - Did not find DWCA config files! Has the git repo been cloned?",
        dao.dwcaGetDataSetNames().length > 0);
  }

  @Ignore
  @Test
  public void test_dwcaGetDataSet() throws NoSuchDataSetException, IOException {

    // Download a DwCA file
    String tmpDir = System.getProperty("java.io.tmpdir");
    File file = new File(tmpDir + "/dwca-test.zip");
    logger.info("DWCA test file: {}", file.getAbsolutePath());
    if (file.exists()) {
      file.delete();
    }
    FileOutputStream fos = new FileOutputStream(file);
    SpecimenDao dao = new SpecimenDao();
    dao.dwcaGetDataSet("aves", fos);
    fos.close();
    assertTrue("01 - DwCA file is empty.", file.length() > 0);

    // Check the DwCA file contents
    List<String> zipEntries = new ArrayList<>();
    String dwcaFile = file.getAbsolutePath();
    Path dest = Paths.get(tmpDir + "/DwcaTest");
    File destDir = dest.toFile();
    if (!destDir.exists()) destDir.mkdirs();

    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(dwcaFile));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      zipEntries.add(zipEntry.getName());
      FileOutputStream out = new FileOutputStream(new File(dest + "/" + zipEntry.getName()));
      int len;
      while ((len = zis.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      out.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();

    assertTrue("02 - DwCA file does not include the right ammount of files.", zipEntries.size() == 4);
    assertTrue("03 - eml.xml is missing.", zipEntries.contains("eml.xml"));
    assertTrue("04 - meta.xml is missing.", zipEntries.contains("meta.xml"));
    assertTrue("05 - Occurrence.txt is missing.", zipEntries.contains("Occurrence.txt"));
    assertTrue("06 - Multimedia.txt is missing.", zipEntries.contains("Multimedia.txt"));
    
    // Test Occurrence.txt
    File occurrenceFile = new File(destDir.getPath(), "Occurrence.txt");
    InputStream is = new FileInputStream(occurrenceFile);
    
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    settings.setHeaderExtractionEnabled(true);
    CsvParser parser = new CsvParser(settings);
    List<Record> allRecords = parser.parseAllRecords(new InputStreamReader(is, "UTF-8"));
    assertEquals("07 - Number of records in DwCA file is incorrect.", 2, allRecords.size());
    
    Set<String> ids = new HashSet<>();
    for (Record record : allRecords) {
      ids.add(record.getString("catalogNumber"));
    }
    assertTrue("08a - Document missing from DwCA.", ids.contains(pMajor.getSourceSystemId()));
    assertTrue("08b - Document missing from DwCA.", ids.contains(lFuscus1.getSourceSystemId()));

    // TODO Add test for Multimedia.txt

    
    // Clean up
    file.delete();
    File[] testFiles = destDir.listFiles();
    if (testFiles != null) {
      for (File f : testFiles) {
        f.delete();
      }
      destDir.delete();
    }
  }
  

  /*
   * Just make sure we don't get exceptions. No assertions about contents of zip archive yet (TODO).
   */
  @Ignore
  @Test
  public void test_dwcaQuery() throws InvalidQueryException, IOException {

    // Download a DwCA file
    String tmpDir = System.getProperty("java.io.tmpdir");
    File file = new File(tmpDir + "/dwca-test.zip");
    if (file.exists()) {
      file.delete();
    }
    QuerySpec qs = new QuerySpec();
    String field = "identifications.defaultClassification.genus";
    String[] values = new String[] {"Parus", "Larus", "Malus"};
    qs.addCondition(new QueryCondition(field, "IN", values));

    FileOutputStream fos = new FileOutputStream(file);
    SpecimenDao dao = new SpecimenDao();
    dao.dwcaQuery(qs, fos);
    fos.close();
    assertTrue("01 - DwCA file is empty.", file.length() > 0);

    // Check the DwCA file contents
    List<String> zipEntries = new ArrayList<>();
    String dwcaFile = file.getAbsolutePath();
    Path dest = Paths.get(tmpDir + "/DwcaTest");
    File destDir = dest.toFile();
    if (!destDir.exists()) destDir.mkdirs();

    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new FileInputStream(dwcaFile));
    ZipEntry zipEntry = zis.getNextEntry();
    while (zipEntry != null) {
      zipEntries.add(zipEntry.getName());
      FileOutputStream out = new FileOutputStream(new File(dest + "/" + zipEntry.getName()));
      int len;
      while ((len = zis.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      out.close();
      zipEntry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();

    assertTrue("02 - DwCA file does not include the right ammount of files.", zipEntries.size() == 3);
    assertTrue("03 - eml.xml is missing.", zipEntries.contains("eml.xml"));
    assertTrue("04 - meta.xml is missing.", zipEntries.contains("meta.xml"));
    assertTrue("05 - Occurrence.txt is missing.", zipEntries.contains("Occurrence.txt"));
    
    File occurrenceFile = new File(destDir.getPath(), "Occurrence.txt");
    InputStream is = new FileInputStream(occurrenceFile);
    
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setLineSeparator("\n");
    settings.setHeaderExtractionEnabled(true);
    CsvParser parser = new CsvParser(settings);
    List<Record> allRecords = parser.parseAllRecords(new InputStreamReader(is, "UTF-8"));
    assertEquals("06 - Number of records in DwCA file is incorrect.", 4, allRecords.size());
    
    Set<String> ids = new HashSet<>();
    for (Record record : allRecords) {
      ids.add(record.getString("catalogNumber"));
    }
    assertTrue("06a - Document missing from DwCA.", ids.contains(pMajor.getSourceSystemId()));
    assertTrue("06b - Document missing from DwCA.", ids.contains(lFuscus1.getSourceSystemId()));
    assertTrue("06c - Document missing from DwCA.", ids.contains(lFuscus2.getSourceSystemId()));
    assertTrue("06d - Document missing from DwCA.", ids.contains(mSylvestris.getSourceSystemId()));
        
    // Clean up
    file.delete();
    File[] testFiles = destDir.listFiles();
    if (testFiles != null) {
      for (File f : testFiles) {
        f.delete();
      }
      destDir.delete();
    }
  }

}
