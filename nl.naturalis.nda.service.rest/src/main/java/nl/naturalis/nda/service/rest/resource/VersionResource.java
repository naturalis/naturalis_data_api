package nl.naturalis.nda.service.rest.resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.service.rest.util.ResourceUtil;

@Path("/version")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class VersionResource {

	@EJB
	Registry registry;


	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String show(@Context UriInfo uriInfo)
	{
		try {
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream("/version.properties"));
			StringWriter sw = new StringWriter(128);
			PrintWriter pw = new PrintWriter(sw);
			pw.println("Netherlands Biodiversity API (NBA)");
			pw.println();
			String version = props.getProperty("git.tag").substring(8);
			pw.println("version: " + version);
			pw.println("build date: " + props.getProperty("built"));
			pw.println("Git branch: " + props.getProperty("git.branch"));
			pw.println("Git commit: " + props.getProperty("git.commit"));
			return sw.toString();
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}


	@GET
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Properties json(@Context UriInfo uriInfo)
	{
		try {
			Properties props = new Properties();
			props.load(getClass().getResourceAsStream("/version.properties"));
			return props;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}

}
