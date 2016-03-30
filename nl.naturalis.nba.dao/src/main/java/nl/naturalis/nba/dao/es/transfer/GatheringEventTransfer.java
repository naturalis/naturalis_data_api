package nl.naturalis.nba.dao.es.transfer;

import nl.naturalis.nba.api.model.Agent;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.MultiMediaGatheringEvent;
import nl.naturalis.nba.dao.es.types.ESGatheringEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer class used for transferring {@link ESGatheringEvent} to {@link GatheringEvent}
 *
 * @author Byron Voorbach
 */
public class GatheringEventTransfer {

    /**
     * Static method to transfer a {@link ESGatheringEvent} to a {@link GatheringEvent}
     *
     * @param esGatheringEvent the object to transfer
     * @return the newly created {@link GatheringEvent}
     */
    public static GatheringEvent transfer(ESGatheringEvent esGatheringEvent) {
        GatheringEvent gatheringEvent = new GatheringEvent();

        doTransfer(esGatheringEvent, gatheringEvent);

        return gatheringEvent;
    }

    /**
     * Static method to transfer a {@link ESGatheringEvent} to a {@link MultiMediaGatheringEvent}
     *
     * @param esGatheringEvent the object to transfer
     * @return the newly created {@link MultiMediaGatheringEvent}
     */
    public static MultiMediaGatheringEvent transferToMultiMedia(ESGatheringEvent esGatheringEvent) {
        MultiMediaGatheringEvent gatheringEvent = new MultiMediaGatheringEvent();

        doTransfer(esGatheringEvent, gatheringEvent);

        return gatheringEvent;
    }

    private static void doTransfer(ESGatheringEvent esGatheringEvent, GatheringEvent gatheringEvent) {
        if (esGatheringEvent != null) {
            gatheringEvent.setProjectTitle(esGatheringEvent.getProjectTitle());
            gatheringEvent.setWorldRegion(esGatheringEvent.getWorldRegion());
            gatheringEvent.setContinent(esGatheringEvent.getContinent());
            gatheringEvent.setCountry(esGatheringEvent.getCountry());
            gatheringEvent.setIso3166Code(esGatheringEvent.getIso3166Code());
            gatheringEvent.setProvinceState(esGatheringEvent.getProvinceState());
            gatheringEvent.setIsland(esGatheringEvent.getIsland());
            gatheringEvent.setLocality(esGatheringEvent.getLocality());
            gatheringEvent.setCity(esGatheringEvent.getCity());
            gatheringEvent.setSublocality(esGatheringEvent.getSublocality());
            gatheringEvent.setLocalityText(esGatheringEvent.getLocalityText());
            gatheringEvent.setMethod(esGatheringEvent.getMethod());
            gatheringEvent.setAltitude(esGatheringEvent.getAltitude());
            gatheringEvent.setAltitudeUnifOfMeasurement(esGatheringEvent.getAltitudeUnifOfMeasurement());
            gatheringEvent.setDepth(esGatheringEvent.getDepth());
            gatheringEvent.setDepthUnitOfMeasurement(esGatheringEvent.getDepthUnitOfMeasurement());
            gatheringEvent.setDateTimeBegin(esGatheringEvent.getDateTimeBegin());
            gatheringEvent.setDateTimeEnd(esGatheringEvent.getDateTimeEnd());

            List<Agent> gatheringAgents = new ArrayList<>();
            if (esGatheringEvent.getGatheringOrganizations() != null) {
                gatheringAgents.addAll(esGatheringEvent.getGatheringOrganizations());
            }
            if (esGatheringEvent.getGatheringPersons() != null) {
                gatheringAgents.addAll(esGatheringEvent.getGatheringPersons());
            }
            gatheringEvent.setGatheringAgents(gatheringAgents);

            gatheringEvent.setSiteCoordinates(GatheringSiteCoordinatesTransfer.transfer(esGatheringEvent
                                                                                                .getSiteCoordinates()));
        }
    }
}
