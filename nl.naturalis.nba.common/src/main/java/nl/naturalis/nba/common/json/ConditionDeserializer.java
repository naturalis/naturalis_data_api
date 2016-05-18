package nl.naturalis.nba.common.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import nl.naturalis.nba.api.query.Condition;

public class ConditionDeserializer extends JsonDeserializer<Condition>  {

	@Override
	public Condition deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		JsonNode node = jp.getCodec().readTree(jp);
		JsonNode n = node.get("operator");
		Double lat = (n == null || n.isNull() || n.textValue() == null) ? null : Double.valueOf(n.textValue());
		n = node.get("longitudeDecimal");
		Double lon = (n == null || n.isNull() || n.textValue() == null) ? null : Double.valueOf(n.textValue());
		return null;
	}

}
