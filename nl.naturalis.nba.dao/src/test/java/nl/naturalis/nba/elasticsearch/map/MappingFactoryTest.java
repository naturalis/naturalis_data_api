package nl.naturalis.nba.elasticsearch.map;

import org.junit.Test;

import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;


public class MappingFactoryTest {

	@Test
	public void testGetMapping()
	{
		MappingFactory mf = new MappingFactory();
		MappingSerializer ms = MappingSerializer.getInstance();
		ms.setPretty(true);
		String s = ms.serialize(mf.getMapping(ESSpecimen.class));
		System.out.println(s);
	}

}
