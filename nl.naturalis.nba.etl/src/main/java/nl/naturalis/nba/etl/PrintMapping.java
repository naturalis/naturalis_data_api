package nl.naturalis.nba.etl;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingSerializer;

/**
 * Prints the Elasticsearch mapping for the provided type.
 * 
 * @author Ayco Holleman
 *
 */
public class PrintMapping {

	private static final String modelPackage = Specimen.class.getPackage().getName();

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		if (args.length == 0) {
			System.err.println("Please specify type");
			System.exit(1);
		}
		Class<? extends IDocumentObject> type = null;
		String className = modelPackage + "." + args[0];
		try {
			type = (Class<? extends IDocumentObject>) Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			System.err.println("Cannot generate mapping for " + args[0]);
			System.exit(1);
		}
		Mapping<?> mapping = MappingFactory.getMapping(type);
		@SuppressWarnings("rawtypes")
		MappingSerializer serializer = new MappingSerializer(true);
		serializer.serialize(System.out, mapping);
	}

}
