package nl.naturalis.nba.common.es.map;

import nl.naturalis.nba.api.annotations.GeoShape;

/**
 * A {@code GeoShapeField} is a {@link SimpleField} with Elasticsearch data type
 * {@link ESDataType#GEO_SHAPE geo_shape}.
 * 
 * @author Ayco Holleman
 *
 */
public class GeoShapeField extends SimpleField {

	private String precision = GeoShape.DEFAULT_PRECISION;
	private boolean points_only;

	public GeoShapeField()
	{
		super(ESDataType.GEO_SHAPE);
	}

	public String getPrecision()
	{
		return precision;
	}

	public void setPrecision(String precision)
	{
		this.precision = precision;
	}

	public boolean isPoints_only()
	{
		return points_only;
	}

	public void setPoints_only(boolean points_only)
	{
		this.points_only = points_only;
	}

}
