package nl.naturalis.nba.common.es.map;

public class PrimitiveField extends ESField {

	protected Index index;

	public PrimitiveField(ESDataType type)
	{
		this.type = type;
	}

	public Index getIndex()
	{
		return index;
	}

	public void setIndex(Index index)
	{
		this.index = index;
	}

}
