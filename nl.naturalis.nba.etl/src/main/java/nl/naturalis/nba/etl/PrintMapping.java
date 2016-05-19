package nl.naturalis.nba.etl;

import org.domainobject.util.ClassUtil;

import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESType;

/**
 * Prints the Elasticsearch mapping for the provided type.
 * 
 * @author Ayco Holleman
 *
 */
public class PrintMapping {

	private static final String esModelPackage = ESSpecimen.class.getPackage().getName();

	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.err.println("Please specify type");
			System.exit(1);
		}
		Class<?> c = null;
		String cn = esModelPackage + "." + args[0];
		try {
			c = Class.forName(cn);
		}
		catch (ClassNotFoundException e) {
			cn = esModelPackage + ".ES" + args[0];
			try {
				c = Class.forName(cn);
			}
			catch (ClassNotFoundException e1) {
				System.err.println("No such type: " + args[0]);
				System.exit(1);
			}
		}
		if (!ClassUtil.isA(c, ESType.class)) {
			System.err.println("Not an Elasticsearch type: " + args[0]);
			System.exit(1);
		}
		@SuppressWarnings("unchecked")
		Class<? extends ESType> c2 = (Class<? extends ESType>) c;
		MappingFactory mf = new MappingFactory();
		Mapping mapping = mf.getMapping(c2);
		MappingSerializer ms = MappingSerializer.getInstance();
		ms.setPretty(true);
		System.out.println(ms.serialize(mapping));
	}

}
