package nl.naturalis.nba.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import nl.naturalis.nba.common.es.ESDateInputTest;
import nl.naturalis.nba.common.es.map.MappingFactoryTest;
import nl.naturalis.nba.common.es.map.MappingInfoTest;
import nl.naturalis.nba.common.json.JsonUtilTest;

@RunWith(Suite.class)
@SuiteClasses({
	JsonUtilTest.class,
	ESDateInputTest.class,
	PathUtilTest.class,
	PathValueReaderTest.class,
	MappingFactoryTest.class,
	MappingInfoTest.class
})
public class AllTests {}
