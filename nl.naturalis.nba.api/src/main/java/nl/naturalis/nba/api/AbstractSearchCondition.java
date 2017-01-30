package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;

public class AbstractSearchCondition {

	protected UnaryBooleanOperator not;
	protected ComparisonOperator operator;
	protected Object value;

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
	public AbstractSearchCondition negate()
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

}