package nl.naturalis.nba.api;

import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;

/**
 * <p>
 * A {@code QuerySpec} models an NBA query. All information required by the
 * various {@code query} methods in the API take a {@link QuerySpec} object to
 * drive the query process.
 * </p>
 * <h3>Providing query specifications through the REST API</h3>
 * <p>
 * Whenever a method in the formal API takes a {@code QuerySpec} object, the
 * corresponding REST API gives you two options the encode the {@code QuerySpec}
 * object in the URL. One option is to provide a {@code _querySpec} query
 * parameter whose value is the JSON-encoded {@code QuerySpec} object (i.e. the
 * {@code QuerySpec} object serialized to JSON). For example:
 * </p>
 * 
 * <pre>
 * http://api.biodiversitydata.nl/v2/specimen/query?_querySpec=%7B%22conditions%22%3A%5B%7B%22field%22%3A%22sourceSystem.code%22%2C%22operator%22%3A%22EQUALS%22%2C%22value%22%3A%22BRAHMS%22%7D%5D%2C%22from%22%3A0%2C%22size%22%3A0%7D
 * </pre>
 * <p>
 * Since these URLs are hard to read and construct for humans, you can also
 * encode the query specification as follows:
 * </p>
 * <p>
 * <ol>
 * <li>Every query parameter that does not start with an underscore is turned
 * into a {@link QueryCondition query condition}. For example:
 * 
 * <pre>
 * http://api.biodiversitydata.nl/v2/specimen/query?sourceSystem.code=CRS&recordBasis=FossileSpecimen
 * </pre>
 * 
 * <li>The {@code _fields} parameter can be used to set the fields you want
 * returned in the response. You can specify multiple fields by separating them
 * with a comma. See {@link #setFields(List) setFields}.
 * <li>The {@code _from} parameter can be used to specify an result set offset.
 * See {@link #setFrom(Integer) setFrom}.
 * <li>The {@code _size} parameter can be used to specify the maximum number of
 * documents to return. See {@link #setSize(Integer) setSize}.
 * <li>The {@code _sortFields} parameter can be used to specify the fields on
 * which to sort. You can specify multiple fields as well as sort directions by
 * using commas to separate the fields and colons the separate field from sort
 * direction. See {@link #setSortFields(List) setSortFields}. For example:
 * 
 * <pre>
 * http://api.biodiversitydata.nl/v2/specimen/query?sourceSystem.code=CRS&_sortFields=recordBasis:ASC,unitID:DESC
 * </pre>
 * 
 * <li>The {@code _logicalOperator} parameter can be used to specify the logical
 * operator joining the query conditions (either AND or OR). See
 * {@link #setLogicalOperator(LogicalOperator) setLogicalOperator}.
 * <li>The {@code _ignoreCase} parameter can be used to issue a case-insensitive
 * search.
 * </ol>
 * </p>
 * <p>
 * You cannot mix the two encoding options. You must <b>either</b> provide a
 * {@code _querySpec} query parameter <b>or</b> use the combination of
 * parameters listed above. Complex queries with operators other than
 * {@link ComparisonOperator#EQUALS} or with nested query conditions are not
 * possible with the second option.
 * </p>
 * <h3>Non-scoring queries</h3>
 * <p>
 * You can turn a {@code QuerySpec} into a so-called non-scoring query by
 * setting the {@link #setConstantScore(boolean) constantScore} property to
 * {@code true}. This will disable the calculation of relevance scores for
 * documents returned from the query, which usually improves performance. Note
 * that score calculation can also be disabled for individual
 * {@link QueryCondition query conditions} within the {@code QuerySpec}. In that
 * case those particular conditions do not contribute to the over-all score of
 * the document while the other conditions still do. Disabling score calculation
 * at the {@code QuerySpec}-level is more rigorous than disabling score
 * calculation at the {@code QueryCondition}-level. Even if you disable scoring
 * for each and every individual query condition within the {@code QuerySpec},
 * the final score may still end up being something else than 1. This is because
 * Elasticsearch can and will also calculate a score from the boolean expression
 * that results from combining all conditions using {@link LogicalOperator#AND
 * AND} or {@link LogicalOperator#OR OR}. If you disable score calculation at
 * the {@code QuerySpec}-level you are guaranteed that no score whatsoever will
 * be calculated.
 * </p>
 * <h3>Example</h3>
 * <p>
 * Here is an example of building a query specification.
 * </p>
 * <p>
 * <b>First, by way of reference, the SQL equivalent:</b>
 * </p>
 * <p>
 * 
 * <pre>
 * SELECT *
 *   FROM Specimen
 *  WHERE (gatheringEvent.gatheringPersons.fullName LIKE '%burg%' AND unitID = 'ZMA.MAM.100')
 *     OR UPPER(phaseOrStage) = 'EGG'
 *  ORDER BY unitID DESC
 *  LIMIT 100, 50
 * </pre>
 * </p>
 * <p>
 * <b>Next, using a {@code QuerySpec} object:</b>
 * </p>
 * <p>
 * 
 * <pre>
 * Condition condition1 = new Condition("gatheringEvent.gatheringPersons.fullName", LIKE, "burg");
 * condition1.and("unitID", "=", "ZMA.MAM.100");
 * Condition condition2 = new Condition("phaseOrStage", EQUALS_IC, "EGG");
 * QuerySpec query = new QuerySpec();
 * query.addCondition(condition1);
 * query.addCondition(condition2);
 * query.setLogicalOperator(OR);
 * query.sortBy("unitID", false);
 * query.setFrom(100);
 * query.setSize(50);
 * </pre>
 * </p>
 * <p>
 * <b>Finally, the JSON representation of the {@code QuerySpec} (needed for the
 * REST API):</b>
 * </p>
 * 
 * <pre>
 * {
 *    "conditions" : [ {
 *        "field" : "gatheringEvent.gatheringPersons.fullName",
 *        "operator" : "LIKE",
 *        "value" : "burg",
 *        "and" : [ {
 *            "field" : "unitID",
 *            "operator" : "EQUALS",
 *            "value" : "ZMA.MAM.100"
 *        } ]
 *        }, {
 *        "field" : "phaseOrStage",
 *        "operator" : "EQUALS_IC",
 *        "value" : "EGG"
 *        } ],
 *    "logicalOperator" : "OR",
 *    "sortFields" : [ {
 *        "path" : "unitID",
 *        "ascending" : false
 *    } ],
 *    "from" : 100,
 *    "size" : 50
 * }
 * </pre>
 * 
 * @author Ayco Holleman
 *
 */
public class QuerySpec {

	private boolean constantScore;
	private List<Path> fields;
	private List<QueryCondition> conditions;
	private LogicalOperator logicalOperator;
	private List<SortField> sortFields;
	private Integer from;
	private Integer size;

	/**
	 * Whether or not this is a non-scoring query.
	 * 
	 * @return
	 */
	public boolean isConstantScore()
	{
		return constantScore;
	}

	/**
	 * Make this a non-scoring query.
	 * 
	 * @param constantScore
	 */
	public void setConstantScore(boolean constantScore)
	{
		this.constantScore = constantScore;
	}

	/**
	 * Specifies one or more fields to be returned in the query response. Akin
	 * to an SQL SELECT clause. The field must belong to the document type being
	 * queried. See {@link #getFields() getFields}.
	 * 
	 * @param fields
	 */
	public void addFields(String... fields)
	{
		if (this.fields == null) {
			int sz = Math.max(fields.length, 8);
			this.fields = new ArrayList<>(sz);
		}
		for (String field : fields) {
			this.fields.add(new Path(field));
		}
	}

	/**
	 * Adds the specified query condition to this {@code QuerySpec} instance.
	 * 
	 * @param condition
	 */
	public void addCondition(QueryCondition condition)
	{
		if (conditions == null) {
			conditions = new ArrayList<>(5);
		}
		conditions.add(condition);
	}

	/**
	 * Causes the documents in the result set to be sorted in ascending order of
	 * the specified field.
	 * 
	 * @param field
	 */
	public void sortBy(String field)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		sortFields.add(new SortField(field));
	}

	/**
	 * Causes the documents in the result set to be sorted on the specified
	 * field with the sort order determined by the {@code ascending} argument.
	 * 
	 * @param field
	 */
	public void sortBy(String field, SortOrder sortOrder)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		sortFields.add(new SortField(field, sortOrder));
	}

	/**
	 * Returns the fields to return in the query response. This is akin to a SQL
	 * SELECT clause. By default all fields will be selected.
	 * 
	 * @return
	 */
	public List<Path> getFields()
	{
		return fields;
	}

	/**
	 * Sets the fields to return in the query response. This is akin to a SQL
	 * SELECT clause. By default all fields will be selected. Be aware of the
	 * effect this method has when querying data model objects like {@link Taxon
	 * taxa} or {@link Specimen specimens} (e.g. with the
	 * {@link INbaAccess#query(QuerySpec) query} method): you <i>still</i> get
	 * back full-blown {@code Taxon} c.q. {@code Specimen} objects, only with
	 * all non-selected fields set to their default value ({@code null} for
	 * strings, dates and objects, zero for number fields and {@code false} for
	 * boolean fields). Thus, the value of a non-selected field has no relation
	 * to its actual value in the NBA data store. This is especially confusing
	 * if you also specified a {@link QueryCondition} for that field (e.g. you
	 * specified it to be {@code true} but in the query result it appears to be
	 * {@code false} - the default boolean value). Therefore: <i>do not read
	 * values of fields you did not select!</i>
	 * 
	 * @return
	 */
	public void setFields(List<Path> fields)
	{
		this.fields = fields;
	}

	/**
	 * Returns the logical operator ({@link LogicalOperator#AND AND} or
	 * {@link LogicalOperator#AND OR}) with which to join the
	 * {@link #getConditions() query conditions}. Defaults to {@code AND}. Note
	 * that any condition may itself contain a list of sibling conditions.
	 * 
	 * @return
	 */
	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator;
	}

	/**
	 * Sets the logical operator ({@link LogicalOperator#AND AND} or
	 * {@link LogicalOperator#AND OR}) with which to join the
	 * {@link #getConditions() query conditions}. Note that any condition may
	 * itself contain a list of sibling conditions.
	 * 
	 * @param operator
	 */
	public void setLogicalOperator(LogicalOperator operator)
	{
		this.logicalOperator = operator;
	}

	/**
	 * Returns the conditions a&#46;k&#46;a&#46; criteria that the documents
	 * must satify.
	 * 
	 * @return
	 */
	public List<QueryCondition> getConditions()
	{
		return conditions;
	}

	/**
	 * Sets the conditions a&#46;k&#46;a&#46; criteria that the documents must
	 * satify.
	 * 
	 * @param conditions
	 */
	public void setConditions(List<QueryCondition> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 * Returns the fields on which to sort the documents in the result set.
	 * 
	 * @return
	 */
	public List<SortField> getSortFields()
	{
		return sortFields;
	}

	/**
	 * Sets the fields on which to sort the documents in the result set.
	 * 
	 * @param sortFields
	 */
	public void setSortFields(List<SortField> sortFields)
	{
		this.sortFields = sortFields;
	}

	/**
	 * Returns the offset in the total result set of documents satisfying the
	 * conditions of this {@code QuerySpec}. Defaults to 0 (zero).
	 * 
	 * @return
	 */
	public Integer getFrom()
	{
		return from;
	}

	/**
	 * Sets the offset in the total result set of documents satisfying the
	 * conditions of this {@code QuerySpec}.
	 * 
	 * @return
	 */
	public void setFrom(Integer from)
	{
		this.from = from;
	}

	/**
	 * Returns the number of documents to return. Defaults to 10.
	 * 
	 * @return
	 */
	public Integer getSize()
	{
		return size;
	}

	/**
	 * Sets the number of documents to return.
	 * 
	 * @param size
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}

}
