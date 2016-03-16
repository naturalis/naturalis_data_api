package nl.naturalis.nba.elasticsearch.schema;

class ESScalar extends ESField {

	private final Type type;
	private Fields fields;
	private Index index;
	private Ngram ngram;

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

	public Fields getFields()
	{
		return fields;
	}

	public void setFields(Fields fields)
	{
		this.fields = fields;
	}

	public Index getIndex()
	{
		return index;
	}

	public void setIndex(Index index)
	{
		this.index = index;
	}

	public Ngram getNgram()
	{
		return ngram;
	}

	public void setNgram(Ngram ngram)
	{
		this.ngram = ngram;
	}

}
