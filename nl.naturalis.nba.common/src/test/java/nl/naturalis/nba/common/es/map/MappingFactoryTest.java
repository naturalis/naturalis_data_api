package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.common.TestUtils.jsonEquals;
import static org.junit.Assert.assertTrue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import nl.naturalis.nba.common.mock.TestPerson;
public class MappingFactoryTest {

  private static Logger logger = LogManager.getLogger(MappingFactoryTest.class);
  
	@Test
	public void testGetMapping()
	{
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		MappingSerializer<TestPerson> serializer = new MappingSerializer<>(true);
		String json = serializer.serialize(mapping);
		logger.info(json);
		String file = "MappingFactoryTest__testGetMapping.json";
		assertTrue("01", jsonEquals(getClass(), json, file));
	}

}
