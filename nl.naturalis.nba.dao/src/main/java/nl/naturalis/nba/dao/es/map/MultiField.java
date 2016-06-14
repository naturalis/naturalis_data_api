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
	static final MultiField DEFAULT_MULTIFIELD;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final MultiField IGNORE_CASE_MULTIFIELD;
	/**
	 * A string field for case-insensitive comparisons.
	 */
	static final MultiField LIKE_MULTIFIELD;

	static {
		DEFAULT_MULTIFIELD = new MultiField(STRING, ANALYZED, null);
		DEFAULT_MULTIFIELD.setName("analyzed");
		IGNORE_CASE_MULTIFIELD = new MultiField(STRING, null, "case_insensitive_analyzer");
		IGNORE_CASE_MULTIFIELD.setName("ignoreCase");
		LIKE_MULTIFIELD = new MultiField(STRING, null, "like_analyzer");
		LIKE_MULTIFIELD.setName("like");
	}

	protected MultiField(ESDataType type, Index index, String analyzer)
	{
		super(type, index, analyzer);
	}

}
