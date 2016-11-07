package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.common.test.TestUtils.jsonEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.naturalis.nba.common.test.TestPerson;
public class MappingFactoryTest {

	@Test
	public void testGetMapping()
	{
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		MappingSerializer serializer = new MappingSerializer(true);
		String json = serializer.serialize(mapping);
		System.out.println(json);
		String file = "MappingFactoryTest__testGetMapping.json";
		assertTrue("01", jsonEquals(getClass(), json, file));
	}

}
