package nl.naturalis.nba.dao.es.transfer;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.dao.es.types.ESGatheringSiteCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer class used for transferring {@link ESGatheringSiteCoordinates} to
 * {@link GatheringSiteCoordinates}
 *
 * @author Byron Voorbach
 */
public class GatheringSiteCoordinatesTransfer {

	/**
	 * Static method to transfer the {@link ESGatheringSiteCoordinates} to a
	 * {@link GatheringSiteCoordinates} object
	 *
	 * @param esSiteCoordinates
	 *            the object to transfer
	 * @return the newly created {@link GatheringSiteCoordinates}
	 */
	public static List<GatheringSiteCoordinates> transfer(
			List<ESGatheringSiteCoordinates> esSiteCoordinates)
	{
		if (esSiteCoordinates == null) {
			return null;
		}

		List<GatheringSiteCoordinates> result = new ArrayList<>();

		for (ESGatheringSiteCoordinates es : esSiteCoordinates) {
			GatheringSiteCoordinates gsc = new GatheringSiteCoordinates();
			gsc.setGridCellCode(es.getGridCellCode());
			gsc.setGridCellSystem(es.getGridCellSystem());
			gsc.setGridLatitudeDecimal(es.getGridLatitudeDecimal());
			gsc.setGridLongitudeDecimal(es.getGridLongitudeDecimal());
			gsc.setGridQualifier(es.getGridQualifier());
			gsc.setLatitudeDecimal(es.getLatitudeDecimal());
			gsc.setLongitudeDecimal(es.getLongitudeDecimal());
			result.add(gsc);
		}

		return result;
	}
}
