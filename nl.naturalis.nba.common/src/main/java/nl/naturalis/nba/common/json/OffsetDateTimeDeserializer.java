package nl.naturalis.nba.common.json;

import java.io.IOException;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import nl.naturalis.nba.common.es.ESDateInput;

class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

	public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException
	{
			return new ESDateInput().parseAsIso8601(jp.getText());
	}

}
