package nl.naturalis.nba.common.json;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import nl.naturalis.nba.common.es.ESDateInput;

class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {

	public OffsetDateTime deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException
	{
    return new ESDateInput(jp.getText()).parse();
	}
//	  try {
//	    return OffsetDateTime.parse(jp.getText(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);      // "yyyy-MM-DD'T'HH:mm:ssTZD"	    
//	  } catch (DateTimeParseException e) {
//	    return new ESDateInput(jp.getText()).parseAsOffsetDateTime(ESDateInput.ES_DATE_FORMAT); // "yyyy-MM-dd'T'HH:mm:ssZ"
//	  }

}
