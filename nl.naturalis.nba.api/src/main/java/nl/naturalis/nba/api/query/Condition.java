package nl.naturalis.nba.api.query;

import static nl.naturalis.nba.api.query.UnaryBooleanOperator.NOT;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Class modeling a query condition. A condition consists of a field name, a
 * {@link ComparisonOperator comparison operator} and a value. For example:
 * "name", EQUALS, "John". A condition can optionally have a list of sibling
 * conditions. These are joined to the containing condition using the AND or OR
 * operator. A condition and its siblings are strongly bound together, as though
 * surrounded by parentheses: {@code (condition AND sibling0 AND
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
 * NOT}. This means that the <b>entire</b> expression that the condition
 * evaluates to is negated. Thus when you negate the above condition, the
 * resulting expression will <b>not</b> be:
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
 * expressed conditions separately to the {@link QuerySpec} object without
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
public class Condition {

	private UnaryBooleanOperator not;
	private String field;
	private ComparisonOperator operator;
	private Object value;
	private List<Condition> and;
	private List<Condition> or;

	public Condition()
	{
	}

	public Condition(String field, String operator, Object value)
	{
		this.field = field;
		this.operator = ComparisonOperator.parse(operator);
		this.value = value;
	}

	public Condition(String field, ComparisonOperator operator, Object value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public Condition(UnaryBooleanOperator not, String field, String operator, Object value)
	{
		this.not = not;
		this.field = field;
		this.operator = ComparisonOperator.parse(operator);
		this.value = value;
	}

	public Condition(UnaryBooleanOperator not, String field, ComparisonOperator operator,
			Object value)
	{
		this.not = not;
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public Condition and(String field, String operator, Object value)
	{
		return and(new Condition(field, operator, value));
	}

	public Condition and(String field, ComparisonOperator operator, Object value)
	{
		return and(new Condition(field, operator, value));
	}

	public Condition and(Condition sibling)
	{
		if (and == null) {
			and = new ArrayList<>(5);
		}
		and.add(sibling);
		return this;
	}

	public Condition andNot(String field, String operator, Object value)
	{
		return andNot(new Condition(field, operator, value));
	}

	public Condition andNot(String field, ComparisonOperator operator, Object value)
	{
		return andNot(new Condition(field, operator, value));
	}

	public Condition andNot(Condition sibling)
	{
		if (and == null) {
			and = new ArrayList<>(5);
		}
		and.add(sibling.negate());
		return this;
	}

	public Condition or(String field, String operator, Object value)
	{
		return or(new Condition(field, operator, value));
	}

	public Condition or(String field, ComparisonOperator operator, Object value)
	{
		return or(new Condition(field, operator, value));
	}

	public Condition or(Condition sibling)
	{
		if (or == null) {
			or = new ArrayList<>(5);
		}
		or.add(sibling);
		return this;
	}

	public Condition orNot(String field, String operator, Object value)
	{
		return orNot(new Condition(field, operator, value));
	}

	public Condition orNot(String field, ComparisonOperator operator, Object value)
	{
		return orNot(new Condition(field, operator, value));
	}

	public Condition orNot(Condition sibling)
	{
		if (or == null) {
			or = new ArrayList<>(5);
		}
		or.add(sibling.negate());
		return this;
	}

	/**
	 * Negates the condition. That is, if it already was a negated condition, it
	 * becomes a non-negated condition again; otherwise it becomes a negated
	 * condition.
	 * 
	 * @return
	 */
	public Condition negate()
	{
		not = (not == null ? NOT : null);
		return this;
	}

	/**
	 * Whether or not this is a negated condition.
	 * 
	 * @return
	 */
	public boolean isNegated()
	{
		return not == NOT;
	}

	public UnaryBooleanOperator getNot()
	{
		return not;
	}

	public void setNot(UnaryBooleanOperator not)
	{
		this.not = not;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public ComparisonOperator getOperator()
	{
		return operator;
	}

	public void setOperator(ComparisonOperator operator)
	{
		this.operator = operator;
	}

	public Object getValue()
	{
		return value;
	}

	public void setValue(Object value)
	{
		this.value = value;
	}

	public List<Condition> getAnd()
	{
		return and;
	}

	public void setAnd(List<Condition> and)
	{
		this.and = and;
	}

	public List<Condition> getOr()
	{
		return or;
	}

	public void setOr(List<Condition> or)
	{
		this.or = or;
	}

}
