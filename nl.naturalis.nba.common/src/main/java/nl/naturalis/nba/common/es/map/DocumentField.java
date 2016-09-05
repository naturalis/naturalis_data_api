package nl.naturalis.nba.common.es.map;

public class DocumentField extends ESField {

	protected Index index;

	public DocumentField(ESDataType type)
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
