package nl.naturalis.nba.elasticsearch.map;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;


public class MappingFactoryTest {

	@Test
	public void testGetMapping()
	{
//		MappingFactory mf = new MappingFactory();
//		MappingSerializer ms = MappingSerializer.getInstance();
//		ms.setPretty(true);
//		String s = ms.serialize(mf.getMapping(ESSpecimen.class));
//		System.out.println(s);
		long l=-59932832400000L;
		Date d = new Date(l);
		System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(d));
	}

}
