package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.License;
import nl.naturalis.nba.api.model.LicenseType;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpatialDatum;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonRelationType;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.api.model.metadata.RestService;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.NbaMetaDataDao;
import nl.naturalis.nba.utils.StringUtil;

@Path("/metadata")
@Stateless
@LocalBean
@Api(value = "metadata")

public class NbaMetaDataResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NbaMetaDataResource.class);
	private static final String SYSPROP_CONF_FILE = "nba.conf.file";

	@EJB
	Registry registry;

  //@formatter:off
	@GET
	@Path("/getSetting/{name}")
	@ApiOperation(
	    value = "Get the value of an NBA setting", 
	    response = Object.class, 
	    notes = "All settings can be queried with /metadata/getSettings")
	@Produces(JSON_CONTENT_TYPE)
	public Object getSetting(
			@ApiParam(
			    value = "name of setting", 
			    required = true, 
			    defaultValue = "operator.CONTAINS.min_term_length") 
			@PathParam("name") String name,
			@Context UriInfo uriInfo)
	{
		try {
			NbaSetting setting = NbaSetting.parse(name);
			return new NbaMetaDataDao().getSetting(setting);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getSettings")
	@ApiOperation(
	    value = "List all publicly available configuration settings for the NBA", 
	    response = Map.class, 
	    notes = "The value of a specific setting can be queried with metadata/getSetting/{name}")
	@Produces(JSON_CONTENT_TYPE)
	public Map<NbaSetting, Object> getSettings(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getSettings();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getSourceSystems")
	@ApiOperation(
	    value = "Get the data sources from which the data was retrieved", 
	    response = SourceSystem[].class, 
	    notes = "Returns code and name of all source systems")
	@Produces(JSON_CONTENT_TYPE)
	public SourceSystem[] getSourceSystems(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getSourceSystems();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledLists")
	@ApiOperation(
	    value = "Get the names of fields for which a controlled vocabulary exists", 
	    response = String[].class, 
	    notes = "Possible values for fields with controlled vocabularies can be queried with metadata/getControlledList/{field}")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getControlledLists(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledLists();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

  @GET
  @Path("/getControlledList/AreaClass")
  @ApiOperation(
      value = "Get allowed values for the field 'AreaClass'", 
      response = License[].class, 
      notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public AreaClass[] getControlledListAreaClass(@Context UriInfo uriInfo)
  {
    try {
      return new NbaMetaDataDao().getControlledListAreaClass();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

	@GET
	@Path("/getControlledList/License")
	@ApiOperation(
	    value = "Get allowed values for the field 'License'", 
	    response = License[].class, 
	    notes = "")
	@Produces(JSON_CONTENT_TYPE)
	public License[] getControlledListLicense(@Context UriInfo uriInfo)
	{
	  try {
	    return new NbaMetaDataDao().getControlledListLicense();
	  } catch (Throwable t) {
	    throw handleError(uriInfo, t);
	  }
	}
	
  @GET
  @Path("/getControlledList/LicenseType")
  @ApiOperation(
      value = "Get allowed values for the field 'LicenseType'", 
      response = LicenseType[].class, 
      notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public LicenseType[] getControlledListLicenseType(@Context UriInfo uriInfo)
  {
    try {
      return new NbaMetaDataDao().getControlledListLicenseType();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }
  
	@GET
	@Path("/getControlledList/Sex")
	@ApiOperation(
	    value = "Get allowed values for the field 'Sex' in a specimen document", 
	    response = Sex[].class, 
	    notes = "")
	@Produces(JSON_CONTENT_TYPE)
	public Sex[] getControlledListSex(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListSex();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	 @GET
	  @Path("/getControlledList/SpatialDatum")
	  @ApiOperation(
	      value = "Get allowed values for the field 'SpatialDatum'", 
	      response = SpatialDatum[].class, 
	      notes = "")
	  @Produces(JSON_CONTENT_TYPE)
	  public SpatialDatum[] getControlledListSpatialDatum(@Context UriInfo uriInfo)
	  {
	    try {
	      return new NbaMetaDataDao().getControlledListSpatialDatum();
	    } catch (Throwable t) {
	      throw handleError(uriInfo, t);
	    }
	  }
	 
	 @GET
	 @Path("/getControlledList/SpecimenTypeStatus")
	 @ApiOperation(
	     value = "Get allowed values for the field 'SpecimenTypeStatus' in a specimen document", 
	     response = SpecimenTypeStatus[].class, 
	     notes = "")
	 @Produces(JSON_CONTENT_TYPE)
	 public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus(@Context UriInfo uriInfo)
	 {
	   try {
	     return new NbaMetaDataDao().getControlledListSpecimenTypeStatus();
	   } catch (Throwable t) {
	     throw handleError(uriInfo, t);
	   }
	 }
	 
	 @GET
	 @Path("/getControlledList/RelationType")
	 @ApiOperation(
	     value = "Get allowed values for the field 'Relationtype'", 
	     response = TaxonRelationType[].class, 
	     notes = "")
	 @Produces(JSON_CONTENT_TYPE)
	 public TaxonRelationType[] getControlledListTaxonRelationType(@Context UriInfo uriInfo)
	 {
	   try {
	     return new NbaMetaDataDao().getControlledListTaxonRelationType();
	   } catch (Throwable t) {
	     throw handleError(uriInfo, t);
	   }
	 }

	@GET
	@Path("/getControlledList/TaxonomicStatus")
	@ApiOperation(
	    value = "Get allowed values for the field 'TaxonomicStatus' in specimen and taxon documents", 
	    response = TaxonomicStatus[].class, 
	    notes = "")
	@Produces(JSON_CONTENT_TYPE)
	public TaxonomicStatus[] getControlledListTaxonomicStatus(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListTaxonomicStatus();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
	
	@GET
	@Path("/getAllowedDateFormats")
	@ApiOperation(
	    value = "Get allowed values for dates in queries", 
	    response = String[].class, 
	    notes = "Queries with other formatted dates will result in a query error")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getAllowedDateFormats(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getAllowedDateFormats();
		} catch (Throwable t) {
				throw handleError(uriInfo, t);
		}
	}

	private static RestService[] restServices;

	@GET
	@Path("/getRestServices")
	@ApiOperation(
	    value = "List all available REST services and their parameters", 
	    response = RestService[].class, 
	    notes = "Lists end point name, http method, response type, and URL")
	@Produces(JSON_CONTENT_TYPE)
	public RestService[] getRestServices(@Context UriInfo uriInfo)
	{
		try {
			if (restServices == null) {

				DaoRegistry registry = DaoRegistry.getInstance();
				String baseUrl = registry.getConfiguration().get("nba.baseurl", true);
				if (baseUrl == null) {
					baseUrl = uriInfo.getBaseUri().toString();
				}
				baseUrl = StringUtil.rtrim(baseUrl, '/');

				List<Class<? extends Annotation>> httpMethodAnnotations;
				httpMethodAnnotations = Arrays.asList(DELETE.class, GET.class, POST.class, PUT.class);

				ConfigurationBuilder config = new ConfigurationBuilder();
				config.setUrls(ClasspathHelper.forPackage(getClass().getPackage().getName()));
				config.setScanners(new MethodAnnotationsScanner());
				Reflections reflections = new Reflections(config);
				Set<Method> resourceMethods = reflections.getMethodsAnnotatedWith(Path.class);
				ArrayList<RestService> services = new ArrayList<>(100);
				for (Method method : resourceMethods) {
					RestService service = new RestService();
					services.add(service);
					Class<?> resourceClass = method.getDeclaringClass();
					String rootPath = resourceClass.getDeclaredAnnotation(Path.class).value();
					String path = method.getDeclaredAnnotation(Path.class).value();
					String endPoint = rootPath + path;
					endPoint = endPoint.replace("//", "/");
					service.setEndPoint(endPoint);
					service.setUrl(baseUrl + endPoint);
					for (Class<? extends Annotation> c : httpMethodAnnotations) {
						if (method.getDeclaredAnnotation(c) != null) {
							service.setMethod(c.getSimpleName());
							break;
						}
					}
					if (method.getDeclaredAnnotation(Produces.class) != null) {
						service.setProduces(method.getDeclaredAnnotation(Produces.class)
								.value()[0]);
					}
					if (method.getDeclaredAnnotation(Consumes.class) != null) {
						service.setConsumes(method.getDeclaredAnnotation(Consumes.class)
								.value()[0]);
					}
				}
				restServices = services.toArray(new RestService[services.size()]);
				Arrays.sort(restServices, new Comparator<RestService>() {

					@Override
					public int compare(RestService o1, RestService o2)
					{
						int i = o1.getEndPoint().compareTo(o2.getEndPoint());
						if (i == 0) {
							i = o1.getMethod().compareTo(o2.getMethod());
						}
						return i;
					}
				});
			}
			return restServices;
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
  //@formatter:on

}
