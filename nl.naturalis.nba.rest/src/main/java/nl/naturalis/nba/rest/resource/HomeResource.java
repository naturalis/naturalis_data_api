package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.io.InputStream;
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

import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

@Path("/")
@Stateless
@LocalBean
@SuppressWarnings("static-method")
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class HomeResource {

	@EJB
	Registry registry;

	@GET
	@Path("/")
	@Produces("text/html;charset=UTF-8")
	public String show(@Context UriInfo uriInfo)
	{
		try {
			InputStream in = getClass().getResourceAsStream("welcome.html");
			String s = new String(IOUtil.readAllBytes(in));
			in.close();
			in = getClass().getResourceAsStream("/version.properties");
			ConfigObject cfg;
			if (in == null) {
				/*
				 * We are inside an ear file that was not built by our ant
				 * scripts; we're most likely running within Eclipse.
				 */
				Properties props = new Properties();
				props.setProperty("git.branch", "test");
				props.setProperty("git.tag", "test");
				props.setProperty("git.commit", "test");
				props.setProperty("build.date", "test");
				props.setProperty("build.number", "test");
				cfg = new ConfigObject(props);
			}
			else {
				cfg = new ConfigObject(in);
				in.close();
			}
			s = s.replace("%git.branch%", cfg.get("git.branch"));
			s = s.replace("%git.tag%", cfg.get("git.tag"));
			s = s.replace("%git.commit%", cfg.get("git.commit"));
			s = s.replace("%build.date%", cfg.get("build.date"));
			s = s.replace("%build.number%", cfg.get("build.number"));
			return s;
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.TEXT_PLAIN)
	public String ping(@Context UriInfo uriInfo)
	{
		try {
			return "Hello NBA client!";
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
