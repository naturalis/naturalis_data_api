package nl.naturalis.nba.api.model.summary;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonCreator;

import nl.naturalis.nba.api.model.GatheringSiteCoordinates;
import nl.naturalis.nba.api.model.INbaModelObject;

/**
 * A miniature version of {@link GatheringSiteCoordinates}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummaryGatheringSiteCoordinates implements INbaModelObject {

	private Point geoShape;

	@JsonCreator
	public SummaryGatheringSiteCoordinates(Point geoShape)
	{
		this.geoShape = geoShape;
	}

	public Point getGeoShape()
	{
		return geoShape;
	}

}
