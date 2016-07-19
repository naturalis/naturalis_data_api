package nl.naturalis.nba.dao.es.map;

import static org.junit.Assert.assertEquals;

import org.domainobject.util.FileUtil;
import org.junit.Test;

import nl.naturalis.nba.dao.es.test.TestPerson;

public class MappingFactoryTest {

	@Test
	public void testGetMapping()
	{
		Mapping mapping = MappingFactory.getMapping(TestPerson.class);
		MappingSerializer serializer = new MappingSerializer(true);
		String json = serializer.serialize(mapping);
		// System.out.println(json);
		String file = "MappingFactoryTest__testGetMapping.json";
		assertEquals("01", getContents(file), json);
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
