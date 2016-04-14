package nl.naturalis.nba.dao.es.map;

public class ESScalar extends ESField {

	/**
	 * A raw, i.e. "not_analyzed" string field.
	 */
	static final ESScalar RAW = new ESScalar(Index.NOT_ANALYZED);
	/**
	 * A string field analyzed using the default analyzer.
	 */
	static final ESScalar DEFAULT = new ESScalar();

	private Index index;
	private String analyzer;

	public ESScalar(ESDataType esDataType)
	{
		super(esDataType);
	}

	private ESScalar(Index index)
	{
		this(ESDataType.STRING);
		this.index = index;
	}

	private ESScalar()
	{
		this(ESDataType.STRING);
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
