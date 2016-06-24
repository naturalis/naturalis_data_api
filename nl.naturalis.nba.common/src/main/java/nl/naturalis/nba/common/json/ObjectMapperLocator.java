package nl.naturalis.nba.common.json;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.query.ComparisonOperator;

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

	public ObjectMapper getObjectMapper(Class<?> forClass)
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
		om.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		SimpleModule module = new SimpleModule();
		module.addDeserializer(GatheringSiteCoordinates.class,
				new GatheringSiteCoordinatesDeserializer());
		module.addDeserializer(PhaseOrStage.class, new PhaseOrStageDeserializer());
		module.addDeserializer(Sex.class, new SexDeserializer());
		module.addDeserializer(ComparisonOperator.class, new OperatorDeserializer());
		om.registerModule(module);
		return om;
	}

}
