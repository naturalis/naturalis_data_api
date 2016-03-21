package nl.naturalis.nba.elasticsearch.map;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class MappingSerializer {

	private static MappingSerializer instance;

	public static MappingSerializer getInstance()
	{
		if (instance == null)
			instance = new MappingSerializer();
		return instance;
	}

	private final ObjectMapper serializer;
	private boolean pretty;

	private MappingSerializer()
	{
		serializer = new ObjectMapper();
		serializer.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		serializer.setSerializationInclusion(Include.NON_NULL);
		serializer.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
	}

	public String serialize(Mapping mapping)
	{
		try {
			if (pretty) {
				return serializer.writerWithDefaultPrettyPrinter().writeValueAsString(mapping);
			}
			return serializer.writeValueAsString(mapping);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isPretty()
	{
		return pretty;
	}

	public void setPretty(boolean pretty)
	{
		this.pretty = pretty;
	}

}
