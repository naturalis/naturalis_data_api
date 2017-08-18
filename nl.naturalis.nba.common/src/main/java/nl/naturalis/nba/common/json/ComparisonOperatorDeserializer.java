package nl.naturalis.nba.common.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import nl.naturalis.nba.api.ComparisonOperator;

@Deprecated
class ComparisonOperatorDeserializer extends JsonDeserializer<ComparisonOperator> {

	public ComparisonOperator deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException
	{
		return ComparisonOperator.parse(jp.getText());
	}

}
