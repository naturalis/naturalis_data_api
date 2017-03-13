package nl.naturalis.nba.api.model.summary;

import org.geojson.GeoJsonObject;
import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.annotations.GeoShape;
import nl.naturalis.nba.api.model.INbaModelObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryGatheringSiteCoordinates implements INbaModelObject {

	private Double longitudeDecimal;
	private Double latitudeDecimal;

	public SummaryGatheringSiteCoordinates()
	{
	}

	public SummaryGatheringSiteCoordinates(Double latitude, Double longitude)
	{
		this.longitudeDecimal = longitude;
		this.latitudeDecimal = latitude;
	}

	@JsonProperty
	@GeoShape(pointsOnly = true)
	public GeoJsonObject getGeoShape()
	{
		if (longitudeDecimal == null || latitudeDecimal == null) {
			return null;
		}
		return new Point(longitudeDecimal, latitudeDecimal);
	}

	public Double getLongitudeDecimal()
	{
		return longitudeDecimal;
	}

	public void setLongitudeDecimal(Double longitudeDecimal)
	{
		this.longitudeDecimal = longitudeDecimal;
	}

	public Double getLatitudeDecimal()
	{
		return latitudeDecimal;
	}

	public void setLatitudeDecimal(Double latitudeDecimal)
	{
		this.latitudeDecimal = latitudeDecimal;
	}

}
