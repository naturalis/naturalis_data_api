package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;

import java.util.List;

public abstract class AbstractSearchCondition<T extends AbstractSearchCondition<T>> {

	UnaryBooleanOperator not;
	ComparisonOperator operator;
	Object value;
	List<T> and;
	List<T> or;

	public AbstractSearchCondition()
	{
		super();
	}
	
	/**
	 * Negates the condition. That is, if it already was a negated condition, it
	 * becomes a non-negated condition again, otherwise it becomes a negated
	 * condition.
	 * 
	 * @return
	 */
	public AbstractSearchCondition<T> negate()
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

	public UnaryBooleanOperator getNot()
	{
		return not;
	}

	public void setNot(UnaryBooleanOperator not)
	{
		this.not = not;
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

	public List<T> getAnd()
	{
		return and;
	}

	public void setAnd(List<T> and)
	{
		this.and = and;
	}

	public List<T> getOr()
	{
		return or;
	}

	public void setOr(List<T> or)
	{
		this.or = or;
	}

}