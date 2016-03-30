package nl.naturalis.nda.client;

import java.io.IOException;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

class GatheringSiteCoordinatesDeserializer extends JsonDeserializer<GatheringSiteCoordinates> {

	@Override
	public GatheringSiteCoordinates deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException
	{
		JsonNode node = jp.getCodec().readTree(jp);
		JsonNode n = node.get("latitudeDecimal");
		Double lat = (n == null || n.isNull() || n.textValue() == null) ? null : Double.valueOf(n.textValue());
		n = node.get("longitudeDecimal");
		Double lon = (n == null || n.isNull() || n.textValue() == null) ? null : Double.valueOf(n.textValue());
		return new GatheringSiteCoordinates(lat, lon);
	}

}
