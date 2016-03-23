package nl.naturalis.nba.api.model;

public class GeoShape {

	public static enum Type
	{
		POINT, POLYGON
	}

	protected Type type;
	
	public Type getType()
	{
		return type;
	}


	public void setType(Type type)
	{
		this.type = type;
	}

}
