package nl.naturalis.nba.api;

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
 * expression, which is then joined with the OR siblings. For example, if you
 * have a condition C with AND siblings A1, A2, A3, and with OR siblings O1, O2,
 * O3, then the resulting expression would be:
 * </p>
 * <code>
 * (C AND A1 AND A2 AND A3) OR O1 OR O2 OR O3
 * </code>
 * </p>
 * <h3>Negating a condition</h3>
 * <p>
 * A condition may be negated using operator {@link UnaryBooleanOperator#NOT
 * NOT}. For example:
 * </p>
 * <code>
 * Condition condition = new Condition(NOT, "name", EQUALS, "John");
 * </code>
 * <p>
 * However, be aware that the effect of this is that the <b>entire</b>
 * expression that the condition evaluates to is negated. Thus when you negate
 * the above condition (with both AND and OR siblings), the resulting expression
 * will <b>not</b> be:
 * </p>
 * <code>
 * ((NOT C) AND A1 AND A2 AND A3) OR O1 OR 02 OR O3
 * </code>
 * <p>
 * Instead, it will be:
 * </p>
 * <code>
 * NOT((C AND A1 AND A2 AND A3) OR O1 OR 02 OR O3)
 * </code>
 * <p>
 * This can quickly become confusing if the siblings themselves are also
 * negated. To avoid this confusion, avoid using the NOT operator and instead
 * use (for example) NOT_EQUALS instead of EQUALS. Alternatively, add negatively
 * expressed conditions one by one to the {@link QuerySpec} object instead of
 * nesting one within the other:
 * </p>
 * <code>
 * QuerySpec querySpec = new QuerySpec();<br>
 * querySpec.addCondition(new Condition(NOT, "genus", EQUALS, "Larus"));<br>
 * querySpec.addCondition(new Condition(NOT, "sourceSystem.code", EQUALS, "CRS"));
 * </code>
 * 
 * @author Ayco Holleman
 *
 */
public class QueryCondition extends AbstractSearchCondition {

	private String field;
	private List<QueryCondition> and;
	private List<QueryCondition> or;

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
		this.field = field;
		this.operator = ComparisonOperator.parse(operator);
		this.value = value;
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
		this.field = field;
		this.operator = operator;
		this.value = value;
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
	public QueryCondition(UnaryBooleanOperator not, String field, String operator, Object value)
	{
		this.not = not;
		this.field = field;
		this.operator = ComparisonOperator.parse(operator);
		this.value = value;
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
	 * Returns the field to which the condition applies.
	 * 
	 * @return
	 */
	public String getField()
	{
		return field;
	}

	/**
	 * Sets the field to which the condition applies.
	 * 
	 * @param field
	 */
	public void setField(String field)
	{
		this.field = field;
	}

	/**
	 * Returns the AND sibling conditions.
	 * 
	 * @return
	 */
	public List<QueryCondition> getAnd()
	{
		return and;
	}

	/**
	 * Sets the AND sibling conditions.
	 * 
	 * @param and
	 */
	public void setAnd(List<QueryCondition> and)
	{
		this.and = and;
	}

	/**
	 * Returns the OR sibling conditions.
	 * 
	 * @return
	 */
	public List<QueryCondition> getOr()
	{
		return or;
	}

	/**
	 * Sets the OR sibling conditions.
	 * 
	 * @param or
	 */
	public void setOr(List<QueryCondition> or)
	{
		this.or = or;
	}

}
