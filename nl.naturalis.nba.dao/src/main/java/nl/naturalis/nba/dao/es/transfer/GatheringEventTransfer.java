package nl.naturalis.nba.dao.es.transfer;

import java.util.List;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;

/**
 * Transfer class used for transferring {@link ESGatheringEvent} to
 * {@link GatheringEvent}
 *
 * @author Byron Voorbach
 */
public class GatheringEventTransfer {

	/**
	 * Static method to transfer a {@link ESGatheringEvent} to a
	 * {@link GatheringEvent}
	 *
	 * @param esGatheringEvent
	 *            the object to transfer
	 * @return the newly created {@link GatheringEvent}
	 */
	public static GatheringEvent transfer(ESGatheringEvent esGatheringEvent)
	{
		GatheringEvent gatheringEvent = new GatheringEvent();
		doTransfer(esGatheringEvent, gatheringEvent);
		return gatheringEvent;
	}

	/**
	 * Static method to transfer a {@link ESGatheringEvent} to a
	 * {@link MultiMediaGatheringEvent}
	 *
	 * @param esGatheringEvent
	 *            the object to transfer
	 * @return the newly created {@link MultiMediaGatheringEvent}
	 */
	public static MultiMediaGatheringEvent transferToMultiMedia(ESGatheringEvent esGatheringEvent)
	{
		MultiMediaGatheringEvent gatheringEvent = new MultiMediaGatheringEvent();
		doTransfer(esGatheringEvent, gatheringEvent);
		return gatheringEvent;
	}

	private static void doTransfer(ESGatheringEvent in, GatheringEvent out)
	{
		if (in != null) {
			out.setProjectTitle(in.getProjectTitle());
			out.setWorldRegion(in.getWorldRegion());
			out.setContinent(in.getContinent());
			out.setCountry(in.getCountry());
			out.setIso3166Code(in.getIso3166Code());
			out.setProvinceState(in.getProvinceState());
			out.setIsland(in.getIsland());
			out.setLocality(in.getLocality());
			out.setCity(in.getCity());
			out.setSublocality(in.getSublocality());
			out.setLocalityText(in.getLocalityText());
			out.setMethod(in.getMethod());
			out.setAltitude(in.getAltitude());
			out.setAltitudeUnifOfMeasurement(in.getAltitudeUnifOfMeasurement());
			out.setDepth(in.getDepth());
			out.setDepthUnitOfMeasurement(in.getDepthUnitOfMeasurement());
			out.setDateTimeBegin(in.getDateTimeBegin());
			out.setDateTimeEnd(in.getDateTimeEnd());
			out.setGatheringPersons(in.getGatheringPersons());
			out.setGatheringOrganizations(in.getGatheringOrganizations());
			List<GatheringSiteCoordinates> coords;
			coords = GatheringSiteCoordinatesTransfer.transfer(in.getSiteCoordinates());
			out.setSiteCoordinates(coords);
		}
	}
}
