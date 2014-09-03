package nl.naturalis.nda.domain;

public class GatheringSiteCoordinates {

	private Double longitudeDecimal;
	private Double latitudeDecimal;


	public GatheringSiteCoordinates()
	{
	}


	public GatheringSiteCoordinates(Double latitude, Double longitude)
	{
		this.longitudeDecimal = longitude;
		this.latitudeDecimal = latitude;
	}


	public GeoPoint getPoint()
	{
		if (longitudeDecimal == null || latitudeDecimal == null) {
			return null;
		}
		return new GeoPoint(longitudeDecimal, latitudeDecimal);
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
