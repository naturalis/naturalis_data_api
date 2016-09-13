package nl.naturalis.nba.common.es.map;

public class GeoShapeField extends PrimitiveField {

	private String precision = "5km";

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

}
