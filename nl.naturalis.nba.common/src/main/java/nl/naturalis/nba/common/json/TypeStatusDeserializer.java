package nl.naturalis.nba.common.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import nl.naturalis.nba.api.model.SpecimenTypeStatus;

/*
 * Deserialization now via @JsonCreator annotation
 */
@Deprecated
class TypeStatusDeserializer extends JsonDeserializer<SpecimenTypeStatus> {

	public SpecimenTypeStatus deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException
	{
		return SpecimenTypeStatus.parse(jp.getText());
	}

}
