package nl.naturalis.nba.common.es.map;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * Serializes {@link Mapping} instances to JSON. Used when creating a new
 * document type within an Elasticsearch index.
 * 
 * @author Ayco Holleman
 *
 */
public class MappingSerializer<T extends IDocumentObject> {

	private final ObjectMapper serializer;
	private final boolean pretty;

	public MappingSerializer()
	{
		this(false);
	}

	public MappingSerializer(boolean pretty)
	{
		serializer = new ObjectMapper();
		serializer.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		serializer.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		serializer.setSerializationInclusion(Include.NON_NULL);
		serializer.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		this.pretty = pretty;
	}

	public String serialize(Mapping<T> mapping)
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

	public void serialize(OutputStream out, Mapping<T> mapping)
	{
		try {
			if (pretty) {
				serializer.writerWithDefaultPrettyPrinter().writeValue(out, mapping);
			}
			serializer.writeValue(out, mapping);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
