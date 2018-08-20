package nl.naturalis.nba.rest.provider;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import javax.enterprise.context.Dependent;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.dao.exception.ConnectionFailureException;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.exception.RESTException;
import nl.naturalis.nba.rest.util.ResourceUtil;

@Provider
@Dependent
public class ExceptionMapperBase implements ExceptionMapper<Throwable> {

  private static final Logger logger = LogManager.getLogger(ExceptionMapperBase.class);

  @Override
  public Response toResponse(Throwable e) {


    /*
     * In the code below we not only check the type of the Throwable, but also the type of the cause
     * of the Throwable, because the REST framework may wrap any custom Exception into something of
     * its own making.
     */

    /*
     * javax.ws.rs.NotFoundException does not come from our code telling the client nicely that a
     * query has returned 0 results. It is Wildfly about to tell the client that the URL cannot be
     * resolved, so this is rather more deep-level and hard-core.
     */
    if (e.getClass() == NotFoundException.class) {
      return Response.serverError().entity(e.toString()).type(TEXT_PLAIN).build();
    }
    if (e.getCause() != null && e.getCause().getClass() == NotFoundException.class) {
      return Response.serverError().entity(e.getCause().toString()).type(TEXT_PLAIN).build();
    }

    /*
     * This exception is thrown when there is a client, but no connection with elasticsearch.
     */
    if (e.getClass() == ConnectionFailureException.class) {
      return Response.serverError().entity(e.toString()).type(TEXT_PLAIN).build();
    }
    if (e.getCause() != null && e.getCause().getClass() == ConnectionFailureException.class) {
      return Response.serverError().entity(e.getCause().toString()).type(TEXT_PLAIN).build();
    }

    /*
     * THIS is us telling the client nicely that something could not be found using the
     * client-provided ID. This is not a "real" error, it's just REST protocol, so we are not going
     * to present the client with stack traces.
     */
    if (e.getClass() == HTTP404Exception.class) {
      HTTP404Exception exc = (HTTP404Exception) e;
      return Response.status(404).type(TEXT_PLAIN).entity(exc.getMessage()).build();
    }
    if (e.getCause() != null && e.getCause().getClass() == HTTP404Exception.class) {
      HTTP404Exception exc = (HTTP404Exception) e.getCause();
      return Response.status(404).type(TEXT_PLAIN).entity(exc.getMessage()).build();
    }

    if (e instanceof RESTException) {
      return jsonResponse(e);
    }
    if (e.getCause() != null && e.getCause() instanceof RESTException) {
      return jsonResponse(e.getCause());
    }

    /*
     * WE SHOULD NOT BE GETTING TO THIS POINT! It means that the developer of some resource method
     * (e.g. in the SpecimenResource class) has forgotten to trap a RuntimeException (probably
     * bubbling up from the DAO layer). Resource methods MUST wrap the entire request-response cycle
     * within a try/catch-all block and wrap any exception into a (subclass of) RESTException. Note
     * that the Java client will not deserialize exceptions properly if you do not do this.
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

    return Response.serverError().entity(e.toString()).type(TEXT_PLAIN).build();
  }

  private static Response jsonResponse(Throwable t) {
    RESTException e = (RESTException) t;
    ResponseBuilder rb = Response.status(e.getStatus());
    rb.type(ResourceUtil.JSON_CONTENT_TYPE);
    rb.entity(e.getInfo());
    return rb.build();
  }

}
