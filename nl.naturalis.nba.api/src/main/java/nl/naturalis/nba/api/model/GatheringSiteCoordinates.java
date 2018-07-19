package nl.naturalis.nba.api.model;

import org.geojson.GeoJsonObject;
import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.annotations.GeoShape;

/*
 * Ignore the geoShape field. It is stored in Elasticsearch to enable geo
 * queries, but its value is always calculated from the lat/long fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatheringSiteCoordinates implements INbaModelObject {

	private Double longitudeDecimal;
	private Double latitudeDecimal;

	private String gridCellSystem;
	private Double gridLatitudeDecimal;
	private Double gridLongitudeDecimal;
	private String gridCellCode;
	private String gridQualifier;
	private Integer coordinateUncertaintyInMeters;
	private String spatialDatum;

	public GatheringSiteCoordinates()
	{
	}

	public GatheringSiteCoordinates(Double latitude, Double longitude)
	{
		this.longitudeDecimal = longitude;
		this.latitudeDecimal = latitude;
	}

	/**
	 * <p>
	 * Returns the site coordinates as a {@link GeoJsonObject}. Use the
	 * {@code geoShape} property for queries using pre-indexed shapes using the
	 * {@link ComparisonOperator#IN} operator. For example:
	 * </p>
	 * <p>
	 * <code>
	 * Condition condition = new Condition("gatheringEvent.gatheringSiteCoordinates.geoShape", IN, "Montana");
	 * </code>
	 * </p>
	 * 
	 * @return
	 */
	@JsonProperty
	@GeoShape(pointsOnly = true)
	public Point getGeoShape()
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

	public String getGridCellSystem()
	{
		return gridCellSystem;
	}

	public void setGridCellSystem(String gridCellSystem)
	{
		this.gridCellSystem = gridCellSystem;
	}

	public Double getGridLatitudeDecimal()
	{
		return gridLatitudeDecimal;
	}

	public void setGridLatitudeDecimal(Double gridLatitudeDecimal)
	{
		this.gridLatitudeDecimal = gridLatitudeDecimal;
	}

	public Double getGridLongitudeDecimal()
	{
		return gridLongitudeDecimal;
	}

	public void setGridLongitudeDecimal(Double gridLongitudeDecimal)
	{
		this.gridLongitudeDecimal = gridLongitudeDecimal;
	}

	public String getGridCellCode()
	{
		return gridCellCode;
	}

	public void setGridCellCode(String gridCellCode)
	{
		this.gridCellCode = gridCellCode;
	}

	public String getGridQualifier()
	{
		return gridQualifier;
	}

	public void setGridQualifier(String gridQualifier)
	{
		this.gridQualifier = gridQualifier;
	}

  public Integer getCoordinateUncertaintyInMeters() {
    return coordinateUncertaintyInMeters;
  }

  public void setCoordinateUncertaintyInMeters(Integer coordinateUncertaintyInMeters) {
    if (coordinateUncertaintyInMeters > 0) {
      this.coordinateUncertaintyInMeters = coordinateUncertaintyInMeters;
    }
  }

  public String getSpatialDatum() {
    return spatialDatum;
  }

  public void setSpatialDatum(String spatialDatum) {
    this.spatialDatum = spatialDatum;
  }

}
