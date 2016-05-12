package nl.naturalis.nba.client;

import java.io.IOException;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.Organization;
import nl.naturalis.nba.api.model.Person;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

class AgentDeserializer extends JsonDeserializer<Agent> {

	@Override
	public Agent deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		JsonNode node = jp.getCodec().readTree(jp);
		if (isPersonNode(node)) {
			return createPerson(node);
		}
		return createOrganization(node);
	}


	private static Agent createPerson(JsonNode node)
	{
		String agentText = node.get("agentText").textValue();
		String fullName = node.get("fullName").textValue();
		Person person = new Person();
		person.setAgentText(agentText);
		person.setFullName(fullName);
		return person;
	}


	private static Agent createOrganization(JsonNode node)
	{
		String agentText = node.get("agentText").textValue();
		String name = node.get("name").textValue();
		Organization organization = new Organization();
		organization.setAgentText(agentText);
		organization.setName(name);
		return organization;
	}


	private static boolean isPersonNode(JsonNode node)
	{
		return node.has("fullName");
	}

}
