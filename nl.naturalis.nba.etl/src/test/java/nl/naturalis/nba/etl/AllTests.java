package nl.naturalis.nba.etl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import nl.naturalis.nba.etl.brahms.BrahmsImportUtilTest;
import nl.naturalis.nba.etl.brahms.BrahmsMultiMediaTransformerTest;
import nl.naturalis.nba.etl.brahms.BrahmsSpecimenTransformerTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({ 
    
    ETLUtilTest.class,
    BrahmsImportUtilTest.class,
    BrahmsSpecimenTransformerTest.class,
    BrahmsMultiMediaTransformerTest.class
})
//@formatter:on
public class AllTests {

}
