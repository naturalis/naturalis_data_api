package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Class modeling a query condition. A condition consists of a field name, a
 * {@link ComparisonOperator} and a value. For example: "name", EQUALS, "John".
 * A condition can optionally have a list of sibling conditions. These are
 * joined to the containing condition using the AND or OR operator. A condition
 * and its siblings are strongly bound together, as though surrounded by
 * parentheses: {@code (condition AND sibling0 AND
 * sibling1)}. Because each sibling may itself also have a list of sibling
 * conditions, this allows you to nest logical expressions like
 * {@code (A AND (B OR C OR (D AND E)) AND F)}.
 * </p>
 * <h3>Combining AND and OR siblings</h3>
 * <p>
 * Following common precedence rules, if a condition has both AND siblings and
 * OR siblings, the condition itself and its AND siblings make up one boolean
 * expression, which is then joined as a whole with the OR siblings. For
 * example, if you have a condition C with AND siblings A1, A2, A3, and with OR
 * siblings O1, O2, O3, then the resulting expression would be:
 * </p>
 * 
 * <pre>
 * (C AND A1 AND A2 AND A3) OR O1 OR O2 OR O3
 * </pre>
 * 
 * <h3>Negating a condition</h3>
 * <p>
 * A condition may be negated using operator {@link UnaryBooleanOperator#NOT
 * NOT}. For example:
 * </p>
 * 
 * <pre>
 * 
 * Condition condition = new Condition(NOT, "name", EQUALS, "John");
 * </pre>
 * <p>
 * However, be aware that the effect of this is that the <b>entire</b>
 * expression that the condition evaluates to is negated. Thus when you negate
 * the above condition (with both AND and OR siblings), the resulting expression
 * will <b>not</b> be:
 * </p>
 * 
 * <pre>
 * ((NOT C) AND A1 AND A2 AND A3) OR O1 OR 02 OR O3
 * </pre>
 * <p>
 * Instead, it will be:
 * </p>
 * 
 * <pre>
 * NOT((C AND A1 AND A2 AND A3) OR O1 OR 02 OR O3)
 * </pre>
 * <p>
 * This can quickly become confusing if the siblings themselves are also
 * negated. To avoid this confusion, avoid using the NOT operator and instead
 * use (for example) NOT_EQUALS instead of EQUALS. Alternatively, add negatively
 * expressed conditions one by one to the {@link QuerySpec} object instead of
 * nesting one within the other:
 * </p>
 * 
 * <pre>
 * QuerySpec querySpec = new QuerySpec();
 * querySpec.addCondition(new Condition(NOT, "genus", EQUALS, "Larus"));
 * querySpec.addCondition(new Condition(NOT, "sourceSystem.code", EQUALS, "CRS"));
 * </pre>
 * 
 * <h3>The ALWAYS TRUE query condition</h3>
 * 
 * <p>
 * Analogous to SQL query conditions that always evaluate to true (like
 * {@code WHERE 1 = 1}), a {@code QueryCondition} whose field, operator and
 * value are all {@code null}, will be interpreted as an ALWAYS TRUE query
 * condition. In other words: the following condition always evaluates to true:
 * </p>
 * 
 * <pre>
 * 
 * QueryCondition TRUE = new QueryCondition();
 * </pre>
 * <p>
 * Note that you can still add AND siblings to this query condition. (You could
 * also add OR siblings, but it would be pointless because whatever OR sibling
 * you add, the condition as a whole will still evaluate to true.) Also note
 * that you can always write you {@link QuerySpec} without having to resort to
 * the ALWAYS TRUE condition, but it might be easier to conceive your query
 * with the ALWAYS TRUE condition at your disposal.
 * </p>
 * 
 * <h3>Scoring and non-scoring conditions</h3>
 * <p>
 * There is a difference between <i>whether</i> a document satisfies a query
 * condition on the one hand and <i>how well</i> it satisfies the query
 * condition on the other. By default a score is calculated for how well a
 * document satisfies a query condition. You can disable this by setting the
 * {@link #setConstantScore(boolean) constantScore} property to {@code true}.
 * This will turn the condition into a simple yes-or-no filter, which usually
 * improves performance. Note though that this only makes sense with text
 * searches using operator {@link ComparisonOperator#MATCHES MATCHES} or
 * {@link ComparisonOperator#LIKE LIKE}. It specifically does not make sense to
 * disable scoring for:
 * <ul>
 * <li>Other operators besides {@link ComparisonOperator#MATCHES MATCHES} and
 * {@link ComparisonOperator#LIKE LIKE}. These already are intrinsically
 * non-scoring. (For example, a string either {@link ComparisonOperator#EQUALS
 * EQUALS} another string or it does not. The semantics of the {@code EQUALS}
 * operator does not allow for gradations.)
 * <li>Conditions on fields with a non-textual data type
 * <li>Negated conditions. You can calculate how well a document matches a
 * search phrase and you can also ascertain that document does <i>not</i> match
 * a search phrase, but it does not makes sense to ask how badly it matches the
 * search phrase. In other words, negated conditions are also intrinsically
 * non-scoring. (For the same reason it does not make sense to disable scoring
 * when using operator {@link ComparisonOperator#NOT_MATCHES NOT_MATCHES} or
 * {@link ComparisonOperator#NOT_LIKE NOT_LIKE}.)
 * </ul>
 * </p>
 * 
 * @author Ayco Holleman
 *
 */
public class QueryCondition {

	private UnaryBooleanOperator not;
	private Path field;
	private ComparisonOperator operator;
	private Object value;
	private List<QueryCondition> and;
	private List<QueryCondition> or;
	private boolean constantScore;
	private float boost = 1F;

	/**
	 * Creates an empty search condition.
	 */
	public QueryCondition()
	{
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public QueryCondition(QueryCondition other)
	{
		not = other.not;
		field = other.field;
		operator = other.operator;
		value = other.value;
		constantScore = other.constantScore;
		boost = other.boost;
		if (other.and != null) {
			and = new ArrayList<>(other.and.size());
			for (QueryCondition c : other.and) {
				and.add(new QueryCondition(c));
			}
		}
		if (other.or != null) {
			or = new ArrayList<>(other.or.size());
			for (QueryCondition c : other.or) {
				or.add(new QueryCondition(c));
			}
		}
	}

	/**
	 * Creates a condition for the specified field, comparing it to the
	 * specified value using the specified operator.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 */
	public QueryCondition(String field, String operator, Object value)
	{
		this(null, field, ComparisonOperator.parse(operator), value);
	}

	/**
	 * Creates a condition for the specified field, comparing it to the
	 * specified value using the specified operator.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 */
	public QueryCondition(String field, ComparisonOperator operator, Object value)
	{
		this(null, field, operator, value);
	}

	/**
	 * Creates a condition for the specified field, comparing it to the
	 * specified value using the specified operator.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 */
	public QueryCondition(Path field, ComparisonOperator operator, Object value)
	{
		this(null, field, operator, value);
	}

	/**
	 * Creates a negated condition for the specified field, comparing it to the
	 * specified value using the specified operator.
	 * 
	 * @param not
	 * @param field
	 * @param operator
	 * @param value
	 */
	public QueryCondition(UnaryBooleanOperator not, String field, ComparisonOperator operator,
			Object value)
	{
		this(not, new Path(field), operator, value);
	}

	public QueryCondition(UnaryBooleanOperator not, Path field, ComparisonOperator operator,
			Object value)
	{
		this.not = not;
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	/**
	 * Adds an AND sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public QueryCondition and(String field, String operator, Object value)
	{
		return and(new QueryCondition(field, operator, value));
	}

	/**
	 * Adds an AND sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public QueryCondition and(String field, ComparisonOperator operator, Object value)
	{
		return and(new QueryCondition(field, operator, value));
	}

	/**
	 * Adds an AND sibling condition to this {@code Condition}.
	 * 
	 * @param sibling
	 * @return
	 */
	public QueryCondition and(QueryCondition sibling)
	{
		if (and == null) {
			and = new ArrayList<>(5);
		}
		and.add(sibling);
		return this;
	}

	/**
	 * Adds an OR sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public QueryCondition or(String field, String operator, Object value)
	{
		return or(new QueryCondition(field, operator, value));
	}

	/**
	 * Adds an OR sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public QueryCondition or(String field, ComparisonOperator operator, Object value)
	{
		return or(new QueryCondition(field, operator, value));
	}

	/**
	 * Adds an OR sibling condition to this {@code Condition}.
	 * 
	 * @param sibling
	 * @return
	 */
	public QueryCondition or(QueryCondition sibling)
	{
		if (or == null) {
			or = new ArrayList<>(5);
		}
		or.add(sibling);
		return this;
	}

	/**
	 * Negates the condition. That is, if it already was a negated condition, it
	 * becomes a non-negated condition again, otherwise it becomes a negated
	 * condition.
	 * 
	 * @return
	 */
	public QueryCondition negate()
	{
		not = (not == null ? NOT : null);
		return this;
	}

	/**
	 * Returns whether or not this is a negated condition. Equivalent to
	 * <code>getNot() != null</code>.
	 * 
	 * @return
	 */
	public boolean isNegated()
	{
		return not != null;
	}

	/**
	 * Whether or not this is a negated condition.
	 * 
	 * @return
	 */
	public UnaryBooleanOperator getNot()
	{
		return not;
	}

	/**
	 * Determines whether or not this is a negated condition. Passing
	 * {@code null} effectively makes this a non-negated condition. Passing
	 * {@link UnaryBooleanOperator#NOT NOT} make it a negated condition.
	 * 
	 * @param not
	 */
	public void setNot(UnaryBooleanOperator not)
	{
		this.not = not;
	}

	/**
	 * Returns the field whose value .
	 * 
	 * @param field
	 */
	public Path getField()
	{
		return field;
	}

	/**
	 * Sets the field to which the condition applies.
	 * 
	 * @param field
	 */
	public void setField(Path field)
	{
		this.field = field;
	}

	/**
	 * Returns operator used to constrain the field's value.
	 * 
	 * @return
	 */
	public ComparisonOperator getOperator()
	{
		return operator;
	}

	/**
	 * Sets operator used to constrain the field's value.
	 * 
	 * @param operator
	 */
	public void setOperator(ComparisonOperator operator)
	{
		this.operator = operator;
	}

	/**
	 * Returns the value to constrain the field to.
	 * 
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the value to constrain the field to.
	 * 
	 * @param value
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 * Returns the AND sibling conditions of this condition.
	 * 
	 * @return
	 */
	public List<QueryCondition> getAnd()
	{
		return and;
	}

	/**
	 * Sets the AND sibling conditions of this condition.
	 * 
	 * @param and
	 */
	public void setAnd(List<QueryCondition> and)
	{
		this.and = and;
	}

	/**
	 * Returns the OR sibling conditions of this condition.
	 * 
	 * @return
	 */
	public List<QueryCondition> getOr()
	{
		return or;
	}

	/**
	 * Sets the OR sibling conditions of this condition.
	 * 
	 * @param and
	 */
	public void setOr(List<QueryCondition> or)
	{
		this.or = or;
	}

	/**
	 * Whether or not this is a non-scoring condition (a&#46;k&#46;a&#46; a
	 * filter).
	 * 
	 * @return
	 */
	public boolean isConstantScore()
	{
		return constantScore;
	}

	/**
	 * Specifies whether or not this is a non-scoring condition
	 * (a&#46;k&#46;a&#46; a filter).
	 * 
	 * @param constantScore
	 */
	public void setConstantScore(boolean constantScore)
	{
		this.constantScore = constantScore;
	}

	/**
	 * Returns the boost that documents should receive if they satisfy this
	 * condition.
	 * 
	 * @return
	 */
	public float getBoost()
	{
		return boost;
	}

	/**
	 * Sets the boost that documents should receive if they satisfy this
	 * condition.
	 * 
	 * @return
	 */
	public void setBoost(float boost)
	{
		this.boost = boost;
	}

}
