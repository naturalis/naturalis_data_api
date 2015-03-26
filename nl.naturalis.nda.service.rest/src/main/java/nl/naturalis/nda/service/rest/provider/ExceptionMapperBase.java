package nl.naturalis.nda.service.rest.provider;

import javax.enterprise.context.Dependent;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import nl.naturalis.nda.service.rest.exception.HTTP200Exception;

@Provider
@Dependent
public class ExceptionMapperBase implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable e)
	{
		if(e instanceof HTTP200Exception) {
			HTTP200Exception exc = (HTTP200Exception) e;
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if(e.getCause() instanceof HTTP200Exception) {
			HTTP200Exception exc = (HTTP200Exception) e.getCause();
			return Response.ok().type(MediaType.APPLICATION_JSON).entity(exc.getInfo()).build();
		}
		if (e instanceof WebApplicationException) {
			WebApplicationException exc = (WebApplicationException) e;
			return exc.getResponse();
		}
		if (e.getCause() instanceof WebApplicationException) {
			WebApplicationException exc = (WebApplicationException) e.getCause();
			return exc.getResponse();
		}
		String warning = e.getClass().getSimpleName();
		if (e.getMessage() != null) {
			warning += ": " + e.getMessage();
		}
		return Response.status(500).header("Warning", warning).build();
	}

}
