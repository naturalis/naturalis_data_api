package nl.naturalis.nba.api;

import java.util.Collection;
import java.util.HashSet;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.annotation.JsonCreator;

import nl.naturalis.nba.api.model.GeoArea;

/**
 * Symbolic constants for the operators that can be used in a
 * {@link QueryCondition query condition}.
 * 
 * @author Ayco Holleman
 *
 */
public enum ComparisonOperator
{

	/**
	 * Operator used to establish that {@link QueryCondition#getValue() query
	 * value} and {@link QueryCondition#getField() field value} are equal. For
	 * text fields the comparison is done in a case sensitive way. The EQUALS
	 * operator also allows you to search for null values by providing a query
	 * value that is {@code null}. This is equivalent to using the
	 * 
	 * <pre>
	 * IS NULL
	 * </pre>
	 * 
	 * operator in SQL. For example:<br>
	 * 
	 * <pre>
	 * 
	 * // Retrieves all documents in which taxonRank is not set
	 * QueryCondition condition = new QueryCondition("taxonRank", EQUALS, null);
	 * </pre>
	 */
	EQUALS("="),

	/**
	 * Operator used to establish that query value and field value are not
	 * equal. For text fields the comparison is done in a case sensitive way.
	 * The NOT_EQUALS operator also allows you to search for non-null values by
	 * providing a query value that is {@code null}. This is equivalent to using
	 * the
	 * 
	 * <pre>
	 * IS NOT NULL
	 * </pre>
	 * 
	 * operator in SQL. For example:<br>
	 * 
	 * <pre>
	 * 
	 * // Retrieves all documents in which taxonRank is set
	 * QueryCondition condition = new QueryCondition("taxonRank", NOT_EQUALS, null);
	 * </pre>
	 */
	NOT_EQUALS("!="),

	/**
	 * <pre>
	 * EQUALS IGNORE CASE
	 * </pre>
	 * 
	 * &#46; Operator used to establish that query value and field value are
	 * equal ignoring case. Can only be used for text fields. The query value
	 * may be null when using the EQUALS operator. This is equivalent to using
	 * the
	 * 
	 * <pre>
	 * IS NULL
	 * </pre>
	 * 
	 * operator in SQL. Although it only makes sense to use this operator with
	 * text fields, no exception is thrown if you use it with other types of
	 * fields. It behaves then as though you had used the {@link #EQUALS}
	 * operator.
	 */
	EQUALS_IC,

	/**
	 * <pre>
	 * NOT EQUALS IGNORE CASE
	 * </pre>
	 * 
	 * &#46; Operator used to establish that query value and field value are not
	 * equal, even when ignoring case. Can only be used for text fields. The
	 * query value may be null when using the EQUALS operator. This is
	 * equivalent to using the
	 * 
	 * <pre>
	 * IS NOT NULL
	 * </pre>
	 * 
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
	 * <pre>
	 * LESS THAN
	 * </pre>
	 * 
	 * &#46; Operator used to establish that a field&#39;s value is less than
	 * the query value. Can only be used for number fields and date fields.
	 */
	LT("<"),

	/**
	 * <pre>
	 * LESS THAN OR EQUAL
	 * </pre>
	 * 
	 * &#46; Operator used to establish that a field&#39;s value is less than or
	 * equal to the query value. Can only be used for number fields and date
	 * fields.
	 */
	LTE("<="),

	/**
	 * <pre>
	 * GREATER THAN
	 * </pre>
	 * 
	 * &#46; Operator used to establish that a field&#39;s value is greater than
	 * the query value. Can only be used for number fields and date fields.
	 */
	GT(">"),

	/**
	 * <pre>
	 * GREATER THAN OR EQUAL
	 * </pre>
	 * 
	 * &#46; Operator used to establish that a field&#39;s value is greater than
	 * or equal to the query value. Can only be used for number fields and date
	 * fields.
	 */
	GTE(">="),

	/**
	 * <p>
	 * Operator used to establish that a field&#39;s value falls within a given
	 * range. Can only be used for number fields and date fields. When using the
	 * BETWEEN or NOT_BETWEEN operator in a {@link QueryCondition}, the
	 * {@link QueryCondition#getValue() value} property of the condition
	 * <b>must</b> be an array or a {@link Collection} object with exactly two
	 * elements. The first element is used as the "from" value (inclusive). The
	 * second element is used as the "to" value (inclusive). Although you can
	 * choose any {@link Collection} implementation you like, you should not
	 * choose one where you have no control over the order of the elements (like
	 * {@link HashSet}). Example:
	 * </p>
	 * <p>
	 * 
	 * <pre>
	 * 
	 * QueryCondition condition = new QueryCondition("numberOfSpecimen", BETWEEN,
	 * 		new int[] { 10, 20 });
	 * </pre>
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
	 * <li>If the field being queried is <b>not</b> a {@link GeoJsonObject}, the
	 * operator is used to establish that the field&#39;s value is one of a
	 * given set of values. The set of allowed value is specified by the
	 * condition's {@link QueryCondition#getValue() value}. The condition's
	 * value <b>must</b> be an array or a {@link Collection} with zero or more
	 * elements. For example:
	 * 
	 * <pre>
	 * 
	 * QueryCondition condition0 = new QueryCondition("phaseOrStage", IN,
	 * 		new String[] { "embryo", "pupa", "larva" });
	 * QueryCondition condition1 = new QueryCondition("numberOfSpecimen", IN,
	 * 		new int[] { 1, 3, 5, 7, 9 });
	 * </pre>
	 * 
	 * <li>If the field being queried is a {@link GeoJsonObject} <i>and</i> the
	 * condition's value is also a {@code GeoJsonObject} or a GeoJSON string,
	 * documents are returned field's coordinates lie within the shape specified
	 * by the condition's value. For example:
	 * 
	 * <pre>
	 * // Using a GeoJSON string:
	 * String field = "gatheringEvent.siteCoordinates.geoShape";
	 * QueryCondition condition = new QueryCondition(field, IN,
	 * 		"{\"type\": \"polygon\", \"coordinates\": [ /&#42; etc. &#42;/ ]}");
	 * 
	 * // Using a GeoJsonObject:
	 * Polygon polygon = new Polygon();
	 * polygon.setCoordinates( /&#42; etc. &#42;/ );
	 * QueryCondition condition = new QueryCondition(field, IN, polygon);
	 * </pre>
	 * 
	 * <li>If the field being queried is a {@link GeoJsonObject} <i>and</i> the
	 * condition's value is a regular (non-JSON) string or string array, the
	 * condition's value is assumed to denote one or more pre-defined
	 * localities. See {@link IGeoAreaAccess#getLocalities()}. For example:
	 * 
	 * <pre>
	 * 
	 * String field = "gatheringEvent.siteCoordinates.geoShape";
	 * QueryCondition condition0 = new QueryCondition(field, IN, "Amsterdam");
	 * QueryCondition condition1 = new QueryCondition(field, IN,
	 * 		new String[] { "Amsterdam", "Berlin" });
	 * </pre>
	 * 
	 * <br>
	 * In stead of a locality name you can also provide the ID of the
	 * corresponding {@link GeoArea} document (see also {@link IGeoAreaAccess}).
	 * For example:
	 * 
	 * <pre>
	 * 
	 * String field = "gatheringEvent.siteCoordinates.geoShape";
	 * // ID of the GeoArea document for Amsterdam:
	 * QueryCondition condition0 = new QueryCondition(field, IN, "1003624@GEO");
	 * </pre>
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
	 * fields. The {@link QueryCondition#getValue() query value} is broken up
	 * along word boundaries (commas, periods, space characters, etc.) with each
	 * word constituting a separate search term. A match exists if any of the
	 * search terms matches the targeted field. See the <a href=
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
	@JsonCreator
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
		throw new IllegalArgumentException("No such comparison operator: " + s);
	}

	private String symbol;

	private ComparisonOperator()
	{
		this.symbol = null;
	}

	private ComparisonOperator(String symbol)
	{
		this.symbol = symbol;
	}

}
