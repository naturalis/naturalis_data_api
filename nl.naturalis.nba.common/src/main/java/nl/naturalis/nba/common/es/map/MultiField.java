package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.api.annotations.Analyzer.CASE_INSENSITIVE;
import static nl.naturalis.nba.api.annotations.Analyzer.DEFAULT;
import static nl.naturalis.nba.api.annotations.Analyzer.LIKE;

import nl.naturalis.nba.api.annotations.Analyzer;

/**
 * A {@code MultiField} is a virtual field underneath a {@link SimpleField
 * document field} that specifies an extra analyzer to be applied to the data
 * stored in that field. Note that, although {@code MultiField} is a subclass of
 * {@link ESField}, calling {@link #getParent()} on a {@code MultiField} will
 * return {@code null}. In other words, you can navigate from a
 * {@link SimpleField} to its {@code MultiField} children, but not the other way
 * round.
 * 
 * @author Ayco Holleman
 *
 */
public class MultiField extends ESField {

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
		DEFAULT_MULTIFIELD = new MultiField(DEFAULT);
		IGNORE_CASE_MULTIFIELD = new MultiField(CASE_INSENSITIVE);
		LIKE_MULTIFIELD = new MultiField(LIKE);
	}

	private String analyzer;

	private MultiField(Analyzer analyzer)
	{
		super();
		this.name = analyzer.getMultiFieldName();
		this.type = ESDataType.TEXT;
		this.analyzer = analyzer.getName();
	}

	public String getAnalyzer()
	{
		return analyzer;
	}

}
