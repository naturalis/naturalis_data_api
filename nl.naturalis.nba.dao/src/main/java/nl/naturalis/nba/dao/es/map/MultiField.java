package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.ESDataType.STRING;

/**
 * A {@code MultiField} is a virtual field underneath a {@link DocumentField
 * document field} that specifies an extra analyzer to be applied to the data
 * stored in that field. Note that, although {@code MultiField} is a subclass of
 * {@link ESField}, calling {@link #getParent()} on a {@code MultiField} will
 * return {@code null}. In other words, you can navigate from a
 * {@link DocumentField} to its {@code MultiField} children, but not the other
 * way round.
 * 
 * @author Ayco Holleman
 *
 */
public class MultiField extends IndexableField {

	/**
	 * A string field analyzed using the default analyzer.
	 */
	public static final MultiField DEFAULT_MULTIFIELD;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	public static final MultiField IGNORE_CASE_MULTIFIELD;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	public static final MultiField LIKE_MULTIFIELD;

	static {
		DEFAULT_MULTIFIELD = new MultiField("analyzed", STRING, null);
		IGNORE_CASE_MULTIFIELD = new MultiField("ignoreCase", STRING, "case_insensitive_analyzer");
		LIKE_MULTIFIELD = new MultiField("like", STRING, "like_analyzer");
	}

	private MultiField(String name, ESDataType type, String analyzer)
	{
		super();
		this.name = name;
		this.type = type;
		this.analyzer = analyzer;
	}

}
