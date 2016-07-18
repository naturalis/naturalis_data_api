package nl.naturalis.nba.dao.es.map;

public class GeoShapeField extends DocumentField {

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
