package nl.naturalis.nba.elasticsearch.schema;

class Raw {

	static final Raw DEFAULT = new Raw(Type.STRING, Index.NOT_ANALYZED);

	private final Type type;
	private final Index index;

	Raw(Type type, Index index)
	{
		this.type = type;
		this.index = index;
	}

	Type getType()
	{
		return type;
	}

	Index getIndex()
	{
		return index;
	}

}
