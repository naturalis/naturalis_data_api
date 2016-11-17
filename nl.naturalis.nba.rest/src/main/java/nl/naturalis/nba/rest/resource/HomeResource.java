package nl.naturalis.nba.rest.resource;

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
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class HomeResource {

	@EJB
	Registry registry;

	@GET
	@Path("/")
	@Produces("text/html;charset=UTF-8")
	public String show(@Context UriInfo uriInfo)
	{
		InputStream in = getClass().getResourceAsStream("welcome.html");
		String s = new String(IOUtil.readAllBytes(in));
		in = getClass().getResourceAsStream("/version.properties");
		ConfigObject cfg;
		if (in == null) {
			/*
			 * We are inside an ear file that was not built by our ant scripts;
			 * we're most likely running within Eclipse.
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
		}

		s = s.replace("%git.branch%", cfg.get("git.branch"));
		s = s.replace("%git.tag%", cfg.get("git.tag"));
		s = s.replace("%git.commit%", cfg.get("git.commit"));
		s = s.replace("%build.date%", cfg.get("build.date"));
		s = s.replace("%build.number%", cfg.get("build.number"));
		return s;
	}

	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	public String ping(@Context UriInfo uriInfo)
	{
		return "Hello NBA client!";
	}

}
