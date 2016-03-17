package nl.naturalis.nba.elasticsearch.map;

public class ESScalar extends ESField {

	static final ESScalar RAW = new ESScalar(Index.NOT_ANALYZED);

	private final ESDataType type;
	private Index index;
	private String analyzer;

	public ESScalar(ESDataType eSDataType)
	{
		this.type = eSDataType;
	}

	private ESScalar(Index index)
	{
		this.type = ESDataType.STRING;
		this.index = index;
	}

	public ESDataType getType()
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
