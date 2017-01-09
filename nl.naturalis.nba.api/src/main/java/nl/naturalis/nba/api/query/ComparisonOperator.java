package nl.naturalis.nba.api.query;

import java.util.Collection;
import java.util.HashSet;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.IGeoAreaAccess;

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
	 * Operator used to establish that {@link Condition#getValue() query value}
	 * and {@link Condition#getField() field value} are equal. For text fields
	 * the comparison is done in a case sensitive way. The EQUALS operator also
	 * allows you to search for null values by providing a query value that is
	 * {@code null}. This is equivalent to using the <code>IS NULL</code>
	 * operator in SQL. For example:<br>
	 * <code>
	 * // Retrieves all documents in which taxonRank is not set<br>
	 * Condition condition = new Condition("taxonRank", EQUALS, null);
	 * </code>
	 */
	EQUALS("="),

	/**
	 * Operator used to establish that query value and field value are not
	 * equal. For text fields the comparison is done in a case sensitive way.
	 * The NOT_EQUALS operator also allows you to search for non-null values by
	 * providing a query value that is {@code null}. This is equivalent to using
	 * the <code>IS NOT NULL</code> operator in SQL. For example:<br>
	 * <code>
	 * // Retrieves all documents in which taxonRank is set<br>
	 * Condition condition = new Condition("taxonRank", NOT_EQUALS, null);
	 * </code>
	 */
	NOT_EQUALS("!="),

	/**
	 * <code>EQUALS IGNORE CASE</code>&#46; Operator used to establish that
	 * query value and field value are equal ignoring case. Can only be used for
	 * text fields. The query value may be null when using the EQUALS operator.
	 * This is equivalent to using the <code>IS NULL</code> operator in SQL.
	 * Although it only makes sense to use this operator with text fields, no
	 * exception is thrown if you use it with other types of fields. It behaves
	 * then as though you had used the {@link #EQUALS} operator.
	 */
	EQUALS_IC,

	/**
	 * <code>NOT EQUALS IGNORE CASE</code>&#46; Operator used to establish that
	 * query value and field value are not equal, even when ignoring case. Can
	 * only be used for text fields. The query value may be null when using the
	 * EQUALS operator. This is equivalent to using the <code>IS NOT NULL</code>
	 * operator in SQL. Although it only makes sense to use this operator with
	 * text fields, no exception is thrown if you use it with other types of
	 * fields. It behaves then as though you had used the {@link #NOT_EQUALS}
	 * operator.
	 */
	NOT_EQUALS_IC,

	/**
	 * Operator used to establish that a field contains the query value. Can
	 * only be used for text fields. Search term and field value are compared in
	 * a case insensitive way. The query value must not be null.
	 */
	LIKE,

	/**
	 * Operator used to establish that a field does not contain the query
	 * string. Can only be used for text fields. Search term and field value are
	 * compared in a case insensitive way. The query value must not be null.
	 */
	NOT_LIKE,

	/**
	 * <code>LESS THAN</code>&#46; Operator used to establish that a field&#39;s
	 * value is less than the query value. Can only be used for number fields
	 * and date fields.
	 */
	LT("<"),

	/**
	 * <code>LESS THAN OR EQUAL</code>&#46; Operator used to establish that a
	 * field&#39;s value is less than or equal to the query value. Can only be
	 * used for number fields and date fields.
	 */
	LTE("<="),

	/**
	 * <code>GREATER THAN</code>&#46; Operator used to establish that a
	 * field&#39;s value is greater than the query value. Can only be used for
	 * number fields and date fields.
	 */
	GT(">"),

	/**
	 * <code>GREATER THAN OR EQUAL</code>&#46; Operator used to establish that a
	 * field&#39;s value is greater than or equal to the query value. Can only
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
	 * of values or, if the field is a {@link GeoJsonObject}, that it lies
	 * within a certain geographical area. More precisely, this operator is
	 * overloaded as follows:
	 * <ol>
	 * <li>If the field being queried is <i>not</i> a {@link GeoJsonObject}, the
	 * operator is used to establish that the field&#39;s value is one of a
	 * given set of values specified by the condition's
	 * {@link Condition#getValue() value}. The condition's value <b>must</b> be
	 * an array or a {@link Collection} with zero or more elements. For
	 * example:<br>
	 * <br>
	 * <code>
	 * Condition condition0 = new Condition("phaseOrStage", IN, new String[] {"embryo", "pupa", "larva"});<br>
	 * Condition condition1 = new Condition("numberOfSpecimen", IN, new int[] {1, 3, 5, 7, 9});
	 * </code><br>
	 * <br>
	 * <li>If the field being queried is a {@link GeoJsonObject} <i>and</i> the
	 * condition's value is a GeoJSON string or a {@code GeoJsonObject},
	 * documents are returned where coordinates specified by the field lie
	 * within the shape specified by the GeoJSON string/object. The condition's
	 * value <b>must not</b> be an array or {@link Collection} object. For
	 * example:<br>
	 * <br>
	 * <code>
	 * // Using a GeoJSON string:<br>
	 * String field = "gatheringEvent.siteCoordinates.geoShape";<br>
	 * Condition condition = new Condition(field, IN, "{\"type\": \"polygon\", \"coordinates\": [ /&#42; etc. &#42;/ ]}");<br><br>
	 * // Using a GeoJsonObject:<br>
	 * Polygon polygon = new Polygon();<br>
	 * polygon.setCoordinates( /&#42; etc. &#42;/ );<br>
	 * Condition condition = new Condition(field, IN, polygon);<br><br>
	 * </code><br>
	 * <br>
	 * <li>If the field being queried is a {@link GeoJsonObject} <i>and</i> the
	 * condition's value is a regular (non-JSON) string or string array, the
	 * condition's value is assumed to denote one or more pre-defined
	 * localities. See {@link IGeoAreaAccess#getLocalities()}. For example:<br>
	 * <br>
	 * <code>
	 * String field = "gatheringEvent.siteCoordinates.geoShape";<br>
	 * Condition condition0 = new Condition(field, IN, "Amsterdam");<br>
	 * Condition condition1 = new Condition(field, IN, new String[] {"Amsterdam", "Berlin"});<br><br>
	 * </code>
	 * </ol>
	 */
	IN,

	/**
	 * Operator used to establish that a field&#39;s value is none of a given
	 * set of values. See {@link #IN} for the multiple ways in which this
	 * operator can be used.
	 */
	NOT_IN,

	/**
	 * Operator used to trigger a full-text search. Can only be used for text
	 * fields. The {@link Condition#getValue() query value} is broken up along
	 * word boundaries (commas, periods, space characters, etc.) with each word
	 * constituting a separate search term. A match exists if any of the search
	 * terms matches the targeted field. See the <a href=
	 * "https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html">Elasticsearch
	 * documentation</a>.
	 */
	MATCHES,

	/**
	 * Operator used to trigger a full-text search (negating its outcome). Can
	 * only be used for text fields. See {@link #MATCHES}.
	 */
	NOT_MATCHES;

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
