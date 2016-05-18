package nl.naturalis.nba.common.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperLocator {

	private static ObjectMapperLocator instance;

	public static ObjectMapperLocator getInstance()
	{
		if (instance == null) {
			instance = new ObjectMapperLocator();
		}
		return instance;
	}

	private ObjectMapper dfault;

	private ObjectMapperLocator()
	{
		dfault = createDefaultObjectMapper();
	}

	public ObjectMapper getObjectMapper(Class<?> forClass)
	{
		/*
		 * Currently we always serve up the same ObjectMapper instance, whatever
		 * the class of the object to serialize.
		 */
		if (dfault == null) {
			dfault = createDefaultObjectMapper();
		}
		return dfault;
	}

	private static ObjectMapper createDefaultObjectMapper()
	{
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		om.setSerializationInclusion(Include.NON_NULL);
		om.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		return om;
	}

}
