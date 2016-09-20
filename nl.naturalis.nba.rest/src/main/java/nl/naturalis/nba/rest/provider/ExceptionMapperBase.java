package nl.naturalis.nba.rest.provider;

import javax.enterprise.context.Dependent;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nba.rest.exception.HTTP200Exception;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.exception.RESTException;

@Provider
@Dependent
public class ExceptionMapperBase implements ExceptionMapper<Throwable> {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionMapperBase.class);

	@Override
	public Response toResponse(Throwable e)
	{

		/*
		 * In the code below we not only check the type of the Throwable, but
		 * also the type of the cause of the Throwable, because the REST
		 * framework may wrap any custom Exception into something of its own
		 * making.
		 */

		/*
		 * javax.ws.rs.NotFoundException does not come from our code telling you
		 * nicely that a query has returned 0 results. It comes from Wildfly
		 * telling the client that the URL cannot be resolved, so rather serious
		 * and hard-core.
		 */
		if (e.getClass() == NotFoundException.class) {
			return Response.serverError().entity(e.toString()).type(MediaType.TEXT_PLAIN_TYPE)
					.build();
		}
		if (e.getCause() != null && e.getCause().getClass() == NotFoundException.class) {
			return Response.serverError().entity(e.getCause().toString())
					.type(MediaType.TEXT_PLAIN_TYPE).build();
		}

		if (e.getClass() == HTTP200Exception.class) {
			HTTP200Exception exc = (HTTP200Exception) e;
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if (e.getCause() != null && e.getCause().getClass() == HTTP200Exception.class) {
			HTTP200Exception exc = (HTTP200Exception) e.getCause();
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}

		if (e.getClass() == HTTP404Exception.class) {
			HTTP404Exception exc = (HTTP404Exception) e;
			return Response.status(404).type(MediaType.TEXT_PLAIN).entity(exc.getMessage()).build();
		}
		if (e.getCause() != null && e.getCause().getClass() == HTTP404Exception.class) {
			HTTP404Exception exc = (HTTP404Exception) e.getCause();
			return Response.status(404).type(MediaType.TEXT_PLAIN).entity(exc.getMessage()).build();
		}

		if (e instanceof RESTException) {
			return jsonResponse(e);
		}
		if (e.getCause() != null && e.getCause() instanceof RESTException) {
			return jsonResponse(e.getCause());
		}

		/*
		 * WE SHOULD NOT BE GETTING TO THIS POINT! It means some resource method
		 * (e.g. in SpecimenResource) has forgotten to trap a RuntimeException
		 * (probably bubbling up from the DAO layer). Resource methods SHOULD
		 * wrap the entire request-response cycle within a try/catch-all block
		 * and wrap any exception into a (subclass of) RESTException. Note that
		 * the Java client will not deserialize exceptions properly if you do
		 * not do this.
		 */
		logger.error("Exception not wrapped by resource method: {}", e.getClass().getName());

		if (e instanceof WebApplicationException) {
			WebApplicationException exc = (WebApplicationException) e;
			return exc.getResponse();
		}
		if (e.getCause() != null && e.getCause() instanceof WebApplicationException) {
			WebApplicationException exc = (WebApplicationException) e.getCause();
			return exc.getResponse();
		}

		return Response.serverError().entity(e.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
	}

	private static Response jsonResponse(Throwable t)
	{
		RESTException e = (RESTException) t;
		return Response.status(e.getStatus()).type(MediaType.APPLICATION_JSON).entity(e.getInfo())
				.build();
	}

}
