package nl.naturalis.nba.api.model.summary;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	/**
	 * Determines whether this object is the summary of a given
	 * {@code GatheringSiteCoordinates} object, i.e. if the (nested) fields of
	 * the  {@code SummaryGatheringSiteCoordinates} object all match the given 
	 * {@code GatheringSiteCoordinates} object.
	 * 
	 * @param gsc the {@code GatheringSiteCoordinates} object to compare to
	 * @return true of this object is a summary of the object given in argument 
	 */
	public boolean isSummaryOf(GatheringSiteCoordinates gsc)
	{	    	    
	    return this.getGeoShape().equals(gsc.getGeoShape());
	}
	    
	    
	@JsonCreator
	public SummaryGatheringSiteCoordinates(@JsonProperty("geoShape") Point geoShape)
	{
		this.geoShape = geoShape;
	}

	public Point getGeoShape()
	{
		return geoShape;
	}

}
