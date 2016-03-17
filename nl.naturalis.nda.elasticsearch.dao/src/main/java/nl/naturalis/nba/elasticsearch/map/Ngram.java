package nl.naturalis.nba.elasticsearch.map;

class Ngram {

	private final ESDataType esDataType;
	private final String analyzer;

	public Ngram(String analyzer)
	{
		this.esDataType = ESDataType.STRING;
		this.analyzer = analyzer;
	}

	public Ngram(ESDataType eSDataType, String analyzer)
	{
		this.esDataType = eSDataType;
		this.analyzer = analyzer;
	}

	public ESDataType getType()
	{
		return esDataType;
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

}
