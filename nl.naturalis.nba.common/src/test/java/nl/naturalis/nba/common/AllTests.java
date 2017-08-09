package nl.naturalis.nba.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.common.es.map.MappingFactoryTest;
import nl.naturalis.nba.common.es.map.MappingInfoTest;
import nl.naturalis.nba.common.json.JsonUtilTest;

@RunWith(Suite.class)
//@formatter:off
@SuiteClasses({
	JsonUtilTest.class,
	PathUtilTest.class,
	PathValueReaderTest.class,
	MappingFactoryTest.class,
	MappingInfoTest.class
})
//@formatter:on
public class AllTests {

}
