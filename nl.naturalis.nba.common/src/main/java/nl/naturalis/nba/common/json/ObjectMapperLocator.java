package nl.naturalis.nba.common.json;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Produces Jackson {@link ObjectMapper} instances tailored to the type of
 * object to be serialized or deserialized.
 * 
 * @author Ayco Holleman
 *
 */
public class ObjectMapperLocator {

	private static final ObjectMapperLocator instance = new ObjectMapperLocator();

	public static ObjectMapperLocator getInstance()
	{
		return instance;
	}

	private final ObjectMapper dfault;

	private ObjectMapperLocator()
	{
		dfault = createDefaultObjectMapper();
	}

	public ObjectMapper getObjectMapper(Class<?> forType)
	{
		/*
		 * Currently we always serve up the same ObjectMapper instance, whatever
		 * type of object to serialize or deserialize.
		 */
		return dfault;
	}

	public ObjectMapper getObjectMapper(TypeReference<?> forType)
	{
		/*
		 * Currently we always serve up the same ObjectMapper instance, whatever
		 * type of object to serialize or deserialize.
		 */
		return dfault;
	}

	private static ObjectMapper createDefaultObjectMapper()
	{
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(ALL, NONE);
		om.setVisibility(FIELD, ANY);
		om.setSerializationInclusion(NON_NULL);
		om.enable(WRITE_ENUMS_USING_TO_STRING);
		SimpleModule module = new SimpleModule();
		module.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
		module.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
		om.registerModule(module);
		return om;
	}

}
