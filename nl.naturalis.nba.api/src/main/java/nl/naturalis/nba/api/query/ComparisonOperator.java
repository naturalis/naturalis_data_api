package nl.naturalis.nba.api.query;

import java.util.Collection;
import java.util.HashSet;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.GeoPoint;

/**
 * Symbolic constants for the operators that can be used in a {@link Condition
 * query condition}.
 * 
 * @author Ayco Holleman
 *
 */
public enum ComparisonOperator
{

	/**
	 * Operator used to establish that search term and field value are equal.
	 * For text fields the comparison is done in a case sensitive way. The
	 * EQUALS operator also allows you to search for null values by providing a
	 * search term that is {@code null}. This is equivalent to using the
	 * <code>IS NULL</code> operator in SQL. For example:<br>
	 * <code>
	 * // Retrieves all documents in which taxonRank is not set<br>
	 * Condition condition = new Condition("taxonRank", EQUALS, null);
	 * </code>
	 */
	EQUALS("="),

	/**
	 * Operator used to establish that search term and field value are not
	 * equal. For text fields the comparison is done in a case sensitive way.
	 * The NOT_EQUALS operator also allows you to search for non-null values by
	 * providing a search term that is {@code null}. This is equivalent to using
	 * the <code>IS NOT NULL</code> operator in SQL. For example:<br>
	 * <code>
	 * // Retrieves all documents in which taxonRank is set<br>
	 * Condition condition = new Condition("taxonRank", NOT_EQUALS, null);
	 * </code>
	 */
	NOT_EQUALS("!="),

	/**
	 * <code>EQUALS IGNORE CASE</code>&#46; Operator used to establish that
	 * search term and field value are equal ignoring case. Can only be used for
	 * text fields. The search term may be null when using the EQUALS operator.
	 * This is equivalent to using the <code>IS NULL</code> operator in SQL.
	 * Although it only makes sense to use this operator with text fields, no
	 * exception is thrown if you use it with other types of fields. It behaves
	 * then as though you had used the {@link #EQUALS} operator.
	 */
	EQUALS_IC,

	/**
	 * <code>NOT EQUALS IGNORE CASE</code>&#46; Operator used to establish that
	 * search term and field value are not equal, even when ignoring case. Can
	 * only be used for text fields. The search term may be null when using the
	 * EQUALS operator. This is equivalent to using the <code>IS NOT NULL</code>
	 * operator in SQL. Although it only makes sense to use this operator with
	 * text fields, no exception is thrown if you use it with other types of
	 * fields. It behaves then as though you had used the {@link #NOT_EQUALS}
	 * operator.
	 */
	NOT_EQUALS_IC,

	/**
	 * Operator used to establish that a field contains the search term. Can
	 * only be used for text fields. Search term and field value are compared in
	 * a case insensitive way. The search term must not be null.
	 */
	LIKE,

	/**
	 * Operator used to establish that the a field does not contain the search
	 * term. Can only be used for text fields. Search term and field value are
	 * compared in a case insensitive way. The search term must not be null.
	 */
	NOT_LIKE,

	/**
	 * <code>LESS THAN</code>&#46; Operator used to establish that a field&#39;s
	 * value is less than the search term. Can only be used for number fields
	 * and date fields.
	 */
	LT("<"),

	/**
	 * <code>LESS THAN OR EQUAL</code>&#46; Operator used to establish that a
	 * field&#39;s value is less than or equal to the search term. Can only be
	 * used for number fields and date fields.
	 */
	LTE("<="),

	/**
	 * <code>GREATER THAN</code>&#46; Operator used to establish that a
	 * field&#39;s value is greater than the search term. Can only be used for
	 * number fields and date fields.
	 */
	GT(">"),

	/**
	 * <code>GREATER THAN OR EQUAL</code>&#46; Operator used to establish that a
	 * field&#39;s value is greater than or equal to the search term. Can only
	 * be used for number fields and date fields.
	 */
	GTE(">="),

	/**
	 * <p>
	 * Operator used to establish that a field&#39;s value falls within a given
	 * range. Can only be used for number fields and date fields. When using the
	 * BETWEEN or NOT_BETWEEN operator in a {@link Condition}, the
	 * {@link Condition#getValue() value} property of the condition <b>must</b>
	 * be an array or a {@link Collection} object with exactly two elements. The
	 * first element is used as the "from" value (inclusive). The second element
	 * is used as the "to" value (inclusive). Although you can choose any
	 * {@link Collection} implementation you like, you should not choose one
	 * where you have no control over the order of the elements (like
	 * {@link HashSet}). Example:
	 * </p>
	 * <p>
	 * <code>
	 * Condition condition = new Condition("numberOfSpecimen", BETWEEN, new int[] {10, 20});
	 * </code>
	 * </p>
	 */
	BETWEEN,

	/**
	 * Operator used to establish that a field&#39;s value lies outside a given
	 * range. Can only be used for number fields and date fields. See
	 * {@link #BETWEEN}.
	 */
	NOT_BETWEEN,

	/**
	 * <p>
	 * Operator used to establish that a field&#39;s value is one of a given set
	 * of values or, if the field is a {@link GeoPoint} or
	 * {@link GeoJsonObject}, that it lies within a certain area. This operator
	 * is overloaded as follows:
	 * <ol>
	 * <li>If the field that is being queried is neither a {@link GeoPoint} nor
	 * a {@link GeoJsonObject}, the operator is used to establish that the
	 * field&#39;s value is one of a given set of values specified by the
	 * {@link Condition#getValue() value} property of the condition. The
	 * {@code value} property of the condition <b>must</b> be an array or a
	 * {@link Collection} with zero or more elements. For example:<br>
	 * <br>
	 * <code>
	 * Condition condition = new Condition("phaseOrStage", IN, new String[] {"embryo", "pupa", "larva"});
	 * </code><br>
	 * <br>
	 * <li>If the field being queried is a {@link GeoPoint}, the {@code value}
	 * property of the condition <b>must</b> be a GeoJson string or a
	 * {@link GeoJsonObject}.
	 * <li>If the field being queried is a {@link GeoJsonObject}, {@code value}
	 * property <b>must</b> a GeoJson string, a {@link GeoJsonObject}, or the ID
	 * of a {@link GeoArea} document containing the GeoJson string.For
	 * example:<br>
	 * <br>
	 * <code>
	 * // Using GeoJson:
	 * Condition condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, "{\"type\": \"polygon\", \"coordinates\": [/&#42; etc. &#42;/]}");
	 * // Using the ID of a GeoArea document:
	 * Condition condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, "1234@GEO");
	 * </code><br>
	 * <br>
	 * The ID lookups for {@link GeoArea} documents can be done using
	 * {@link IGeoAreaAccess}.
	 * 
	 */
	IN,

	/**
	 * Operator used to establish that a field&#39;s value is none of a given
	 * set of values. See {@link #IN}.
	 */
	NOT_IN;

	/**
	 * Returns the {@link ComparisonOperator} corresponding to the specified
	 * string.
	 * 
	 * @param s
	 * @return
	 */
	public static ComparisonOperator parse(String s)
	{
		if (s != null) {
			for (ComparisonOperator op : values()) {
				if (op.symbol != null && s.equals(op.symbol)) {
					return op;
				}
				if (s.equalsIgnoreCase(op.name())) {
					return op;
				}
			}
		}
		return null;
	}

	private final String symbol;

	private ComparisonOperator()
	{
		this.symbol = null;
	}

	private ComparisonOperator(String symbol)
	{
		this.symbol = symbol;
	}

}
