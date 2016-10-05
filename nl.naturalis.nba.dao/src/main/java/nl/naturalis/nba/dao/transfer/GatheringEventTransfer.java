package nl.naturalis.nba.dao.transfer;

import java.util.List;

import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.dao.types.ESGatheringEvent;
import nl.naturalis.nba.dao.types.ESGatheringSiteCoordinates;

/**
 * Transfer class used for transferring {@link ESGatheringEvent} to
 * {@link GatheringEvent}
 *
 * @author Byron Voorbach
 */
public class GatheringEventTransfer {

	/**
	 * Static method to transfer an {@link ESGatheringEvent} to a
	 * {@link GatheringEvent}
	 *
	 * @param in
	 *            the object to transfer
	 * @return the newly created {@link GatheringEvent}
	 */
	public static GatheringEvent load(ESGatheringEvent in)
	{
		GatheringEvent out = new GatheringEvent();
		doLoad(in, out);
		return out;
	}

	/**
	 * Static method to transfer a {@link GatheringEvent} to an
	 * {@link ESGatheringEvent}
	 * 
	 */
	public static ESGatheringEvent save(GatheringEvent in)
	{
		ESGatheringEvent out = new ESGatheringEvent();
		doSave(in, out);
		return out;
	}

	/**
	 * Static method to transfer an {@link ESGatheringEvent} to a
	 * {@link MultiMediaGatheringEvent}
	 *
	 * @param in
	 *            the object to transfer
	 * @return the newly created {@link MultiMediaGatheringEvent}
	 */
	public static MultiMediaGatheringEvent loadMultiMediaGatheringEvent(ESGatheringEvent in)
	{
		MultiMediaGatheringEvent out = new MultiMediaGatheringEvent();
		doLoad(in, out);
		return out;
	}

	private static void doLoad(ESGatheringEvent in, GatheringEvent out)
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
			coords = GatheringSiteCoordinatesTransfer.load(in.getSiteCoordinates());
			out.setSiteCoordinates(coords);
		}
	}

	private static void doSave(GatheringEvent in, ESGatheringEvent out)
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
			List<ESGatheringSiteCoordinates> coords;
			coords = GatheringSiteCoordinatesTransfer.save(in.getSiteCoordinates());
			out.setSiteCoordinates(coords);
		}
	}
}
