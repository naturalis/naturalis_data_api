package nl.naturalis.nba.dao.format.dwca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.Entity;

public class DwcaWriterTest {
  
  @SuppressWarnings("unused")
  private static final Logger logger = DaoRegistry.getInstance().getLogger(DwcaWriterTest.class);

  @Test
  public void test_getWriter() throws FileNotFoundException, NoSuchDataSetException, DataSetConfigurationException {
    OutputStream out = new FileOutputStream(new File("/tmp/dwca.tst"));
    DwcaConfig config = new DwcaConfig("botany", DwcaDataSetType.SPECIMEN);
    assertEquals(SingleDataSourceDwcaWriter.class.getName(), config.getWriter(out).getClass().getName());
  }

  @Test
  public void test_getEml() throws NoSuchDataSetException, DataSetConfigurationException {
    DwcaConfig config = new DwcaConfig("botany", DwcaDataSetType.SPECIMEN);
    File f = config.getEmlFile();
    assertTrue("01", f.exists() && f.isFile() && f.length() > 0);
    assertEquals( "02", "eml.xml", f.getName());
  }

  @Test
  public void test_getCsvFileName() throws NoSuchDataSetException, DataSetConfigurationException {
    DwcaConfig config = new DwcaConfig("botany", DwcaDataSetType.SPECIMEN);
    Entity entity = config.getCoreEntity();
    String configFile = config.getCsvFileName(entity);
    assertEquals("01", "Occurrence.txt", configFile);
  }
  
	@Test
	public void testWriteDwcaForQuery_01() 
	{
    //TODO
	}

	@Test
	public void testWriteDwcaForDataSet_01()
	{
	  //TODO
	}

}
