package nl.naturalis.nba.rest.provider;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;

@Provider
@Consumes(JSON_CONTENT_TYPE)
@Produces(JSON_CONTENT_TYPE)
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

	@Override
	public ObjectMapper getContext(Class<?> type)
	{
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		return oml.getObjectMapper(type);
	}

}
