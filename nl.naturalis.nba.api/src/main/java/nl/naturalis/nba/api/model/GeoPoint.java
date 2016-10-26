package nl.naturalis.nba.api.model;

public class GeoPoint implements INbaModelObject {

	private double lat;
	private double lon;

	public GeoPoint(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
	}

	public GeoPoint()
	{
	}

	double getLat()
	{
		return lat;
	}

	void setLat(double lat)
	{
		this.lat = lat;
	}

	double getLon()
	{
		return lon;
	}

	void setLon(double lon)
	{
		this.lon = lon;
	}

}
