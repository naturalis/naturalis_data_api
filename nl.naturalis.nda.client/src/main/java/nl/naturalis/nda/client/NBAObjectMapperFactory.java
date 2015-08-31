package nl.naturalis.nda.client;

import nl.naturalis.nda.domain.Agent;
import nl.naturalis.nda.domain.GatheringSiteCoordinates;

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
 * @created Jul 14, 2015
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
