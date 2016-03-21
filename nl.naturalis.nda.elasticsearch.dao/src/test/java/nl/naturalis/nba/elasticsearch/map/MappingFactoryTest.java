package nl.naturalis.nba.elasticsearch.map;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.junit.Test;


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
