package nl.naturalis.nba.elasticsearch.schema;

class Ngram {

	private final Type type;
	private final String index_analyzer;

	Ngram(String analyzer)
	{
		this.type = Type.STRING;
		this.index_analyzer = analyzer;
	}

	Ngram(Type type, String analyzer)
	{
		this.type = type;
		this.index_analyzer = analyzer;
	}

	public Type getType()
	{
		return type;
	}

	public String getIndex_analyzer()
	{
		return index_analyzer;
	}

}
