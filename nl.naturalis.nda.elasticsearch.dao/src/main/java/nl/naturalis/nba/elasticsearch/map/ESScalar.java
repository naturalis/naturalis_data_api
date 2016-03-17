package nl.naturalis.nba.elasticsearch.map;

public class ESScalar extends ESField {

	static final ESScalar RAW = new ESScalar(Index.NOT_ANALYZED);

	private final Type type;
	private Index index;
	private String analyzer;

	public ESScalar(Type type)
	{
		this.type = type;
	}

	private ESScalar(Index index)
	{
		this.type = Type.STRING;
		this.index = index;
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
