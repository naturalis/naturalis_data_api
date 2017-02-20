package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Class modeling a search condition. A condition consists of a field name, a
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
 * </p>
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
 * @author Ayco Holleman
 *
 */
public class SearchCondition {

	private UnaryBooleanOperator not;
	private Path field;
	private ComparisonOperator operator;
	private Object value;
	private List<SearchCondition> and;
	private List<SearchCondition> or;
	private boolean constantScore;
	private float boost = 1F;

	/**
	 * Creates an empty search condition.
	 */
	public SearchCondition()
	{
	}

	/**
	 * Copy constructor.
	 * 
	 * @param other
	 */
	public SearchCondition(SearchCondition other)
	{
		not = other.not;
		field = other.field;
		operator = other.operator;
		value = other.value;
		constantScore = other.constantScore;
		boost = other.boost;
		if (other.and != null) {
			and = new ArrayList<>(other.and.size());
			for (SearchCondition c : other.and) {
				and.add(new SearchCondition(c));
			}
		}
		if (other.or != null) {
			or = new ArrayList<>(other.or.size());
			for (SearchCondition c : other.or) {
				or.add(new SearchCondition(c));
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
	public SearchCondition(String field, String operator, Object value)
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
	public SearchCondition(String field, ComparisonOperator operator, Object value)
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
	public SearchCondition(Path field, ComparisonOperator operator, Object value)
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
	public SearchCondition(UnaryBooleanOperator not, String field, ComparisonOperator operator,
			Object value)
	{
		this(not, new Path(field), operator, value);
	}

	public SearchCondition(UnaryBooleanOperator not, Path field, ComparisonOperator operator,
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
	public SearchCondition and(String field, String operator, Object value)
	{
		return and(new SearchCondition(field, operator, value));
	}

	/**
	 * Adds an AND sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public SearchCondition and(String field, ComparisonOperator operator, Object value)
	{
		return and(new SearchCondition(field, operator, value));
	}

	/**
	 * Adds an AND sibling condition to this {@code Condition}.
	 * 
	 * @param sibling
	 * @return
	 */
	public SearchCondition and(SearchCondition sibling)
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
	public SearchCondition or(String field, String operator, Object value)
	{
		return or(new SearchCondition(field, operator, value));
	}

	/**
	 * Adds an OR sibling condition to this {@code Condition}.
	 * 
	 * @param field
	 * @param operator
	 * @param value
	 * @return
	 */
	public SearchCondition or(String field, ComparisonOperator operator, Object value)
	{
		return or(new SearchCondition(field, operator, value));
	}

	/**
	 * Adds an OR sibling condition to this {@code Condition}.
	 * 
	 * @param sibling
	 * @return
	 */
	public SearchCondition or(SearchCondition sibling)
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
	public SearchCondition negate()
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
	public List<SearchCondition> getAnd()
	{
		return and;
	}

	/**
	 * Sets the AND sibling conditions of this condition.
	 * 
	 * @param and
	 */
	public void setAnd(List<SearchCondition> and)
	{
		this.and = and;
	}

	/**
	 * Returns the OR sibling conditions of this condition.
	 * 
	 * @return
	 */
	public List<SearchCondition> getOr()
	{
		return or;
	}

	/**
	 * Sets the OR sibling conditions of this condition.
	 * 
	 * @param and
	 */
	public void setOr(List<SearchCondition> or)
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
