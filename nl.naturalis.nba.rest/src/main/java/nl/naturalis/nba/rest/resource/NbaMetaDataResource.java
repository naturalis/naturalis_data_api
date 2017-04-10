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

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.api.model.metadata.RestService;
import nl.naturalis.nba.dao.NbaMetaDataDao;
import nl.naturalis.nba.utils.StringUtil;

@Path("/metadata")
@Stateless
@LocalBean
@SuppressWarnings("static-method")
public class NbaMetaDataResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NbaMetaDataResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/getSetting/{name}")
	@Produces(JSON_CONTENT_TYPE)
	public Object getSetting(@PathParam("name") String name, @Context UriInfo uriInfo)
	{
		try {
			NbaSetting setting = NbaSetting.parse(name);
			return new NbaMetaDataDao().getSetting(setting);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getSettings")
	@Produces(JSON_CONTENT_TYPE)
	public Map<NbaSetting, Object> getSettings(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getSettings();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getSourceSystems")
	@Produces(JSON_CONTENT_TYPE)
	public SourceSystem[] getSourceSystems(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getSourceSystems();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledLists")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getControlledLists(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledLists();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledList/Sex")
	@Produces(JSON_CONTENT_TYPE)
	public Sex[] getControlledListSex(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListSex();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledList/PhaseOrStage")
	@Produces(JSON_CONTENT_TYPE)
	public PhaseOrStage[] getControlledListPhaseOrStage(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListPhaseOrStage();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledList/TaxonomicStatus")
	@Produces(JSON_CONTENT_TYPE)
	public TaxonomicStatus[] getControlledListTaxonomicStatus(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListTaxonomicStatus();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getControlledList/SpecimenTypeStatus")
	@Produces(JSON_CONTENT_TYPE)
	public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus(@Context UriInfo uriInfo)
	{
		try {
			return new NbaMetaDataDao().getControlledListSpecimenTypeStatus();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	private static RestService[] restServices;

	@GET
	@Path("/getRestServices")
	@Produces(JSON_CONTENT_TYPE)
	public RestService[] getRestServices(@Context UriInfo uriInfo)
	{
		try {
			if (restServices == null) {

				List<Class<? extends Annotation>> httpMethodAnnotations;
				httpMethodAnnotations = Arrays.asList(DELETE.class, GET.class, POST.class,
						PUT.class);

				String baseUrl = StringUtil.rtrim(uriInfo.getBaseUri().toString(), '/');

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
						service.setProduces(
								method.getDeclaredAnnotation(Produces.class).value()[0]);
					}
					if (method.getDeclaredAnnotation(Consumes.class) != null) {
						service.setConsumes(
								method.getDeclaredAnnotation(Consumes.class).value()[0]);
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
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
