package nl.naturalis.nda.service.rest.provider;

import javax.enterprise.context.Dependent;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.naturalis.nda.service.rest.exception.HTTP200Exception;
import nl.naturalis.nda.service.rest.exception.RESTException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		 * NotFoundException does not come from our code, giving off a nice
		 * RESTful signal that a query has returned 0 results, but from JAX-RS
		 * itself telling the client that the URL cannot be resolved (so a bit
		 * more serious and hard-core).
		 */
		if (e.getClass() == NotFoundException.class) {
			return Response.serverError().entity(e.toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}
		if (e.getCause() != null && e.getCause().getClass() == NotFoundException.class) {
			return Response.serverError().entity(e.getCause().toString()).type(MediaType.TEXT_PLAIN_TYPE).build();
		}

		if (e.getClass() == HTTP200Exception.class) {
			HTTP200Exception exc = (HTTP200Exception) e;
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if (e.getCause() != null && e.getCause().getClass() == HTTP200Exception.class) {
			HTTP200Exception exc = (HTTP200Exception) e.getCause();
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if (e.getClass() == RESTException.class) {
			RESTException exc = (RESTException) e;
			return Response.status(exc.getStatus()).type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if (e.getCause() != null && e.getCause().getClass() == RESTException.class) {
			RESTException exc = (RESTException) e.getCause();
			return Response.status(exc.getStatus()).type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}

		/*
		 * We shouldn't really be getting to this point because resource methods
		 * SHOULD wrap the entire request-response cycle within a try/catch-all
		 * block and wrap any Exception into either an HTTP200Exception or a
		 * RESTException.
		 */
		logger.warn("Exception not wrapped by resource method: " + e.getClass().getName());

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

}
