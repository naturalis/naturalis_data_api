package nl.naturalis.nba.dao.format.dwca;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;

public class DwcaPreparatorTest {
  
  @BeforeClass
  public static void before() {}
  
  @After
  public void after() {}

  @Test
  public void testGetEml() throws DataSetConfigurationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

    DwcaConfig dwcaConfig = mock(DwcaConfig.class);
    
    File f = new File(DwcaPreparatorTest.class.getResource("eml.xml").getFile());
    when(dwcaConfig.getEmlFile()).thenReturn(f);
    
    DwcaPreparator dwcaPreparator = new DwcaPreparator(dwcaConfig);
    Method testPrepareEml = Whitebox.getMethod(DwcaPreparator.class, "prepareEml");
    testPrepareEml.invoke(dwcaPreparator, null);
    
    verify(dwcaConfig).getEmlFile();
    
    // Todo
  }
  
  
  
  
  @Test
  public void testGetMetaXml() throws DataSetConfigurationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    // Todo    
  }

}
