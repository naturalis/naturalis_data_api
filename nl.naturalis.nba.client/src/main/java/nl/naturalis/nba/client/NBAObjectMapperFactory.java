package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
		return instance.objectMapper;
	}

	private final ObjectMapper objectMapper;


	private NBAObjectMapperFactory()
	{
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Agent.class, new AgentDeserializer());
		module.addDeserializer(GatheringSiteCoordinates.class, new GatheringSiteCoordinatesDeserializer());
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(module);
	}

}
