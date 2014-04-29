package nl.naturalis.nda.service.rest.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperBase<E extends Exception> implements ExceptionMapper<E> {

	@Override
	public Response toResponse(E e)
	{
		String warning = e.getClass().getSimpleName();
		if (e.getMessage() != null) {
			warning += ": " + e.getMessage();
		}
		return Response.status(500).header("Warning", warning).build();
	}

}
