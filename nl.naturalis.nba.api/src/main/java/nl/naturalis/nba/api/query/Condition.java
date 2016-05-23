package nl.naturalis.nba.api.query;

import java.util.ArrayList;
import java.util.List;

import static nl.naturalis.nba.api.query.Not.*;

/**
 * <p>
 * Class modeling a query condition. A condition basically consists of a field
 * name, an operator and a value. For example: "name", EQUALS, "John". A
 * condition can optionally have a list of sibling conditions joined together by
 * the AND operator or by the OR operator. A condition and its siblings are
 * strongly bound together, as though surrounded by parentheses:
 * {@code (condition AND sibling0 AND
 * sibling1)}. Because each sibling may itself have a list of sibling
 * confitions, this allows you to construct queries like
 * {@code (A AND (B OR C OR D) AND E)}. Finally, a condition may be negated.
 * This means that the condition and its siblings are negated <i>as a whole</i>,
 * e.g. like {@code NOT(A AND (B OR C OR D) AND E)}.
 * </p>
 * <p>
 * You should not provide both {@link #setAnd(List) AND-joined siblings} and
 * {@link #setOr(List) OR-joined siblings} for one and the same
 * {@code Condition} instance. The API allows this because it makes for elegant
 * code. However, when the condition is validated, a
 * {@link InvalidConditionException} is thrown it contains both AND-joined
 * siblings and OR-joined siblings.
 * </p>
 * 
 * @author Ayco Holleman
 *
 */
public class Condition {

	private Not not;
	private String field;
	private Operator operator;
	private Object value;
	private List<Condition> and;
	private List<Condition> or;

	public Condition()
	{
	}

	public Condition(String field, String operator, Object value)
	{
		this.field = field;
		this.operator = Operator.parse(operator);
		this.value = value;
	}

	public Condition(String field, Operator operator, Object value)
	{
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public Condition(Not not, String field, Operator operator, Object value)
	{
		this.not = not;
		this.field = field;
		this.operator = operator;
		this.value = value;
	}

	public Condition and(String field, Operator operator, Object value)
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

	public Condition andNot(String field, Operator operator, Object value)
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

	public Condition or(String field, Operator operator, Object value)
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

	public Condition orNot(String field, Operator operator, Object value)
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

	public Condition negate()
	{
		not = (not == null ? NOT : null);
		return this;
	}

	public boolean isNegated()
	{
		return not == NOT;
	}

	public Not getNot()
	{
		return not;
	}

	public void setNot(Not not)
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

	public Operator getOperator()
	{
		return operator;
	}

	public void setOperator(Operator operator)
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
