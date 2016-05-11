package nl.naturalis.nba.dao.es.map;

import static nl.naturalis.nba.dao.es.map.ESDataType.STRING;
import static nl.naturalis.nba.dao.es.map.Index.ANALYZED;

/**
 * A {@link MultiField} is a virtual field underneath a {@link DocumentField
 * document field} that specifies an extra analyzer to be applied to the data
 * stored in that field.
 * 
 * @author Ayco Holleman
 *
 */
public class MultiField extends IndexableField {

	/**
	 * A string field analyzed using the default analyzer.
	 */
	static final MultiField DEFAULT_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final MultiField CI_ANALYZED;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final MultiField LIKE_ANALYZED;

	static {
		DEFAULT_ANALYZED = new MultiField(STRING, ANALYZED, null);
		CI_ANALYZED = new MultiField(STRING, null, "case_insensitive_analyzer");
		LIKE_ANALYZED = new MultiField(STRING, null, "like_analyzer");
	}

	protected MultiField(ESDataType type, Index index, String analyzer)
	{
		super(type, index, analyzer);
	}

}
