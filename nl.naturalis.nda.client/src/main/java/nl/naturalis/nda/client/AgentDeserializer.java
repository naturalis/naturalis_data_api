package nl.naturalis.nda.client;

import java.io.IOException;

import nl.naturalis.nda.domain.Agent;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

class AgentDeserializer extends StdDeserializer<Agent> {

	private static final long serialVersionUID = 6204530799443957094L;

	protected AgentDeserializer(Class<?> vc)
	{
		super(vc);
	}

	@Override
	public Agent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		
		return null;
	}

}
