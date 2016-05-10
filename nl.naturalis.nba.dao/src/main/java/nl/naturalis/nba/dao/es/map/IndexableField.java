package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.Index.*;
import static nl.naturalis.nba.dao.es.map.ESDataType.*;

/**
 * A {@code Field} is a {@link ESField} that lets you specify if and how it is
 * indexed. A {@code Field} can be either a top-level {@link DocumentField} or a
 * "multi-field" (specifying an analyzer) underneath it.
 * 
 * @author Ayco Holleman
 *
 */
public class IndexableField extends ESField {

	/**
	 * A raw, "not_analyzed" string field.
	 */
	static final IndexableField RAW;
	/**
	 * A string field analyzed using the default analyzer.
	 */
	static final IndexableField DEFAULT_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final IndexableField CI_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final IndexableField LIKE_ANALYZED;

	static {
		RAW = new IndexableField(STRING, NOT_ANALYZED, null);
		DEFAULT_ANALYZED = new IndexableField(STRING, ANALYZED, null);
		CI_ANALYZED = new IndexableField(STRING, null, "case_insensitive_analyzer");
		LIKE_ANALYZED = new IndexableField(STRING, null, "like_analyzer");
	}

	private Index index;
	private String analyzer;

	public IndexableField(ESDataType esDataType)
	{
		super(esDataType);
	}

	private IndexableField(ESDataType type, Index index, String analyzer)
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
