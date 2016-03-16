package nl.naturalis.nba.elasticsearch.map;


class ESScalar extends ESField {

	private final Type type;
	private Index index;
	private String analyzer;

	ESScalar(Type type)
	{
		this.type = type;
	}

	ESScalar()
	{
		this.type = Type.STRING;
	}

	public Type getType()
	{
		return type;
	}

	public Index getIndex()
	{
		return index;
	}

	public void setIndex(Index index)
	{
		this.index = index;
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

	public void setAnalyzer(String analyzer)
	{
		this.analyzer = analyzer;
	}

}
