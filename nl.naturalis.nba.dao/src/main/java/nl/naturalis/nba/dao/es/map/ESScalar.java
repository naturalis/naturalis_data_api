package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.Index.*;
import static nl.naturalis.nba.dao.es.map.ESDataType.*;

public class ESScalar extends ESField {

	/**
	 * A raw, "not_analyzed" string field.
	 */
	static final ESScalar RAW;
	/**
	 * A string field analyzed using the default analyzer.
	 */
	static final ESScalar DEFAULT_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final ESScalar CI_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final ESScalar LIKE_ANALYZED;

	static {
		RAW = new ESScalar(STRING, NOT_ANALYZED, null);
		DEFAULT_ANALYZED = new ESScalar(STRING, ANALYZED, null);
		CI_ANALYZED = new ESScalar(STRING, null, "case_insensitive_analyzer");
		LIKE_ANALYZED = new ESScalar(STRING, null, "like_analyzer");
	}

	private Index index;
	private String analyzer;

	public ESScalar(ESDataType esDataType)
	{
		super(esDataType);
	}

	private ESScalar(ESDataType type, Index index, String analyzer)
	{
		super(type);
		this.index = index;
		this.analyzer = analyzer;
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
