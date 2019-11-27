package nl.naturalis.nba.utils;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({ 
	
	StringUtilTest.class,
	CollectionUtilTest.class,
	ArrayUtilTest.class,
	ConfigObjectTest.class,
	ClassUtilTest.class,
	FileUtilTest.class,
	IOUtilTest.class,
	ObjectUtilTest.class,
	ExceptionUtilTest.class
	
})
public class AllTests {

}
