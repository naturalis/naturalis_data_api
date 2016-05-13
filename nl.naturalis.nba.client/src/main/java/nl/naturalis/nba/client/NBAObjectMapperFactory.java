package nl.naturalis.nba.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.databind.SerializationFeature.*;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;

/**
 * Factory for producing Jackson {@code ObjectMapper} instances specialized in
 * converting JSON output from the NBA REST API to NBA domain objects. You
 * <i>must</i> use this factory to get hold of {@code ObjectMapper}s, otherwise
 * deserialization will fail. Both the factory itself and the
 * {@code ObjectMapper} is creates are singleton instances.
 * 
 * @author Ayco Holleman
 *
 */
class NBAObjectMapperFactory {

	private static NBAObjectMapperFactory instance;

	public static ObjectMapper getObjectMapper()
	{
		if (instance == null) {
			instance = new NBAObjectMapperFactory();
		}
		return instance.om;
	}

	private final ObjectMapper om;

	private NBAObjectMapperFactory()
	{
		SimpleModule module = new SimpleModule();
		GatheringSiteCoordinatesDeserializer gscd = new GatheringSiteCoordinatesDeserializer();
		module.addDeserializer(GatheringSiteCoordinates.class, gscd);
		om = new ObjectMapper();
		om.registerModule(module);
		om.enable(WRITE_ENUMS_USING_TO_STRING);
	}

}
