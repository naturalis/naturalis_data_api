package nl.naturalis.nda.elasticsearch.dao.transfer;

import nl.naturalis.nda.domain.GatheringSiteCoordinates;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESGatheringSiteCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer class used for transferring {@link ESGatheringSiteCoordinates} to {@link GatheringSiteCoordinates}
 *
 * @author Byron Voorbach
 */
public class GatheringSiteCoordinatesTransfer {

    /**
     * Static method to transfer the {@link ESGatheringSiteCoordinates} to a {@link GatheringSiteCoordinates} object
     *
     * @param esSiteCoordinates the object to transfer
     * @return the newly created {@link GatheringSiteCoordinates}
     */
    public static List<GatheringSiteCoordinates> transfer(List<ESGatheringSiteCoordinates> esSiteCoordinates) {
        List<GatheringSiteCoordinates> gatheringSiteCoordinates = new ArrayList<>();

        for (ESGatheringSiteCoordinates esSiteCoordinate : esSiteCoordinates) {
            gatheringSiteCoordinates.add(new GatheringSiteCoordinates(esSiteCoordinate.getLatitudeDecimal(),
                                                                      esSiteCoordinate.getLongitudeDecimal()));
        }

        return gatheringSiteCoordinates;
    }
}
