package nl.naturalis.nba.elasticsearch.map;

public abstract class ESField {

	protected final ESDataType type;

	public ESField(ESDataType type)
	{
		this.type = type;
	}

	public ESDataType getType()
	{
		return type;
	}

}
