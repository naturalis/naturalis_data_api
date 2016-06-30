package nl.naturalis.nba.api.model;

public class GeoPoint extends NBADomainObject {

	private double[] coordinates;

	public GeoPoint()
	{
	}

	public GeoPoint(double longitude, double latitude)
	{
		this.coordinates = new double[] { longitude, latitude };
	}

	public double[] getCoordinates()
	{
		return coordinates;
	}

	public void setCoordinates(double[] coordinates)
	{
		this.coordinates = coordinates;
	}

}
