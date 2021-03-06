package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.dao.util.es.ESUtil.getNbaMetadata;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.reflections.Reflections;

import io.swagger.annotations.Api;
import io.swagger.annotations.Contact;
import io.swagger.annotations.ExternalDocs;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;

import nl.naturalis.nba.utils.StringUtil;
import nl.naturalis.nba.dao.NbaDao;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

@SwaggerDefinition(info = @Info(title = "Netherlands Biodiversity Api", version = "v2",
    description = "Access to the digitised Natural History collection at the Naturalis Biodiversity Center",
    contact = @Contact(name = "Naturalis Biodiversity Center", url = "https://www.naturalis.nl/nl/",
        email = "support@naturalis.nl"),
    license = @License(name = "GPL",
        url = "https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")),
    basePath = "/v2",
    externalDocs = @ExternalDocs(url = "https://docs.biodiversitydata.nl",
        value = "Documentation and examples"),
    tags = {@Tag(name = "specimen", description = "Access specimens present in the collection"),
        @Tag(name = "taxon", description = "Access taxon information from taxonimic catalogues"),
        @Tag(name = "geo", description = "Access polygons of geographical areas"),
        @Tag(name = "multimedia",
            description = "Access multimedia objects associated with specimens"),
        @Tag(name = "metadata", description = "Miscellaneous information")

    })

@Path("/")
@Api(value = "/", hidden = true)
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class HomeResource {

  @EJB
  Registry registry;

  @GET
  @Path("/")
  @Produces("text/html;charset=UTF-8")
  public String show(@Context UriInfo uriInfo) {
    // Is NBA up and running?
    NbaDao.ping();
    try {
      InputStream in = getClass().getResourceAsStream("welcome.html");
      String template = new String(IOUtil.readAllBytes(in));
      in.close();
      in = getClass().getResourceAsStream("/git.properties");
      ConfigObject values;
      if (in == null) {
        /*
         * We have not been built by maven; we're most likely running within Eclipse.
         */
        Properties props = new Properties();
        props.setProperty("git.branch", "test");
        props.setProperty("git.closest.tag.name", "test");
        props.setProperty("git.commit.id", "test");
        props.setProperty("git.build.time", "test");
        values = new ConfigObject(props);
      } else {
        values = new ConfigObject(in);
        in.close();
      }
      template = template.replace("%git.branch%", StringUtil.emptyIfNull(values.get("git.branch")));
      template = template.replace("%git.closest.tag.name%", StringUtil.emptyIfNull(values.get("git.closest.tag.name")));
      template = template.replace("%git.commit.id%", StringUtil.emptyIfNull(values.get("git.commit.id")));
      template = template.replace("%git.build.time%", StringUtil.emptyIfNull(values.get("git.build.time")));
      return template;
    } catch (IOException t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/release-notes")
  @Produces("text/plain;charset=UTF-8")
  public String releaseNote(@Context UriInfo uriInfo) {
    try {
      InputStream in = getClass().getResourceAsStream("release-notes.txt");
      String s = new String(IOUtil.readAllBytes(in));
      in.close();
      return s;
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/ping")
  @Produces(MediaType.TEXT_PLAIN)
  public String ping(@Context UriInfo uriInfo) {
    try {
      boolean esOK = true;
      try {
        NbaDao.ping();
      } catch (Throwable t) {
        esOK = false;
      }
      if (esOK) {
        return "NBA Service is up and running!";
      }
      return "NBA Service is up, but database is down ...";
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/reference-doc")
  @Produces(MediaType.APPLICATION_JSON)
  public Swagger referenceDoc(@Context UriInfo uriInfo) {
    try {
      // get classes that are annotated in this package
      Reflections reflections = new Reflections(this.getClass().getPackage().getName());
      Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Api.class);

      // return swagger JSON
      return new Reader(new Swagger()).read(classes);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/import-files")
  @Produces(MediaType.APPLICATION_JSON)
  public Map<String, Object> importData() {
    return getNbaMetadata();
  }

}
