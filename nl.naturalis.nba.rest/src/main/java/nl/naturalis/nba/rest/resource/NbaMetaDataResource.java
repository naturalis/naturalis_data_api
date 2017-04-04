package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.dao.NbaMetaDataDao;

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
	public SourceSystem[] getSourceSystems()
	{
		return new NbaMetaDataDao().getSourceSystems();
	}

	@GET
	@Path("/getContolledList/Sex")
	@Produces(JSON_CONTENT_TYPE)
	public Sex[] getControlledListSex()
	{
		return new NbaMetaDataDao().getControlledListSex();
	}

	@GET
	@Path("/getContolledList/PhaseOrStage")
	@Produces(JSON_CONTENT_TYPE)
	public PhaseOrStage[] getControlledListPhaseOrStage()
	{
		return new NbaMetaDataDao().getControlledListPhaseOrStage();
	}

	@GET
	@Path("/getContolledList/TaxonomicStatus")
	@Produces(JSON_CONTENT_TYPE)
	public TaxonomicStatus[] getControlledListTaxonomicStatus()
	{
		return new NbaMetaDataDao().getControlledListTaxonomicStatus();
	}

	@GET
	@Path("/getContolledList/SpecimenTypeStatus")
	@Produces(JSON_CONTENT_TYPE)
	public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus()
	{
		return new NbaMetaDataDao().getControlledListSpecimenTypeStatus();
	}

}
