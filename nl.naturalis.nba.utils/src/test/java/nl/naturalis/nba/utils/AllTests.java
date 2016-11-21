package nl.naturalis.nba.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({ 
	StringUtilTest.class,
	ArrayUtilTest.class,
	ConfigObjectTest.class
	
})
//@formatter:on
public class AllTests {

}
