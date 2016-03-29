package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.elasticsearch.map.Mapping;
import nl.naturalis.nba.elasticsearch.map.MappingFactory;
import nl.naturalis.nba.elasticsearch.map.MappingSerializer;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

/**
 * Prints the Elasticsearch mapping for the provided type.
 * 
 * @author Ayco Holleman
 *
 */
public class PrintMapping {

	private static final String apiModelPackage = Specimen.class.getPackage().getName();
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
				cn = apiModelPackage + "." + args[0];
				try {
					c = Class.forName(cn);
				}
				catch (ClassNotFoundException e2) {
					System.err.println("No such type: " + args[0]);
					System.exit(1);
				}
			}
		}
		MappingFactory mf = new MappingFactory();
		Mapping mapping = mf.getMapping(c);
		MappingSerializer ms = MappingSerializer.getInstance();
		ms.setPretty(true);
		System.out.println(ms.serialize(mapping));
	}

}
