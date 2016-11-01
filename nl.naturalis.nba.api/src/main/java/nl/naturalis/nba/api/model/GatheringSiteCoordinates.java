package nl.naturalis.nba.api.model;

import org.geojson.GeoJsonObject;
import org.geojson.Point;
import org.geojson.Polygon;

import nl.naturalis.nba.api.annotations.Mapped;
import nl.naturalis.nba.api.query.ComparisonOperator;

public class GatheringSiteCoordinates implements INbaModelObject {

	private Double longitudeDecimal;
	private Double latitudeDecimal;

	private String gridCellSystem;
	private Double gridLatitudeDecimal;
	private Double gridLongitudeDecimal;
	private String gridCellCode;
	private String gridQualifier;

	public GatheringSiteCoordinates()
	{
	}

	public GatheringSiteCoordinates(Double latitude, Double longitude)
	{
		this.longitudeDecimal = longitude;
		this.latitudeDecimal = latitude;
	}

	/**
	 * Returns the site coordinates as {@link GeoPoint point}. Use the {@code geoPoint}
	 * property for regular geo queries using the {@link ComparisonOperator#IN} operator.
	 * For example:<br>
	 * <code>
	 * String shape = "{\"type\": \"polygon\", \"coordinates\": [[10,-20],[20,-30],[30,-40],[10,-20]] }";<br>
	 * Condition condition = new Condition("gatheringEvent.gatheringSiteCoordinates.geoPoint", IN, shape);
	 * </code><br>
	 * Instead of the JSON string you could also have provided the corresponding
	 * {@link GeoJsonObject} (a {@link Polygon} in this example).
	 * 
	 * @return
	 */
	@Mapped
	public GeoPoint getGeoPoint()
	{
		if (longitudeDecimal == null || latitudeDecimal == null) {
			return null;
		}
		return new GeoPoint(latitudeDecimal, longitudeDecimal);
	}

	/**
	 * Returns the site coordinates as {@link GeoJsonObject shape}. Since the
	 * {@code GatheringSiteCoordinates} still basically represents a point coordinate, the
	 * actual return type of this method is a geojson {@link Point}. Use the
	 * {@code geoShape} property for queries using pre-indexed shapes using the
	 * {@link ComparisonOperator#IN} operator. For example:<br>
	 * <code>
	 * Condition condition = new Condition("gatheringEvent.gatheringSiteCoordinates.geoShape", IN, "Montana");
	 * </code><br/>
	 * 
	 * @return
	 */
	@Mapped
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

}
