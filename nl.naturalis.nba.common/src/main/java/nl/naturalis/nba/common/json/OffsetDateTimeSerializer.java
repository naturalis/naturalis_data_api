package nl.naturalis.nba.common.json;

import java.io.IOException;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.naturalis.nba.common.es.ESDateInput;

class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
	
	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException
	{
		gen.writeString(ESDateInput.format(value));
	}


}
