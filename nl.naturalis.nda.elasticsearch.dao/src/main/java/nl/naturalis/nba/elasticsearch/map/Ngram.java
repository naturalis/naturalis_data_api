package nl.naturalis.nba.elasticsearch.map;

class Ngram {

	private final Type type;
	private final String analyzer;

	Ngram(String analyzer)
	{
		this.type = Type.STRING;
		this.analyzer = analyzer;
	}

	Ngram(Type type, String analyzer)
	{
		this.type = type;
		this.analyzer = analyzer;
	}

	public Type getType()
	{
		return type;
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

}
