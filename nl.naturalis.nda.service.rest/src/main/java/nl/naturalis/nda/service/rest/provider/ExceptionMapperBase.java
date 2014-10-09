package nl.naturalis.nda.service.rest.provider;

import javax.enterprise.context.Dependent;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Dependent
public class ExceptionMapperBase implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable e)
	{
		String warning = e.getClass().getSimpleName();
		if (e.getMessage() != null) {
			warning += ": " + e.getMessage();
		}
		return Response.status(500).header("Warning", warning).build();
	}

}
