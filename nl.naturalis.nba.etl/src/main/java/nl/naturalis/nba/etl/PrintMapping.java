package nl.naturalis.nba.etl;

import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingSerializer;
import nl.naturalis.nba.dao.types.ESSpecimen;

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
		Class<?> type = null;
		String className = esModelPackage + "." + args[0];
		try {
			type = Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			className = esModelPackage + ".ES" + args[0];
			try {
				type = Class.forName(className);
			}
			catch (ClassNotFoundException e1) {
				System.err.println("Cannot generate mapping for " + args[0]);
				System.exit(1);
			}
		}
		Mapping mapping = MappingFactory.getMapping(type);
		MappingSerializer serializer = new MappingSerializer(true);
		serializer.serialize(System.out, mapping);
	}

}
