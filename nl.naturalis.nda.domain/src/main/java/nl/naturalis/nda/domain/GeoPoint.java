package nl.naturalis.nda.domain;

public class GeoPoint extends GeoShape {

	private double[] coordinates;


	public GeoPoint()
	{
		this.type = Type.POINT;
	}


	public GeoPoint(double longitude, double latitude)
	{
		this.type = Type.POINT;
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
