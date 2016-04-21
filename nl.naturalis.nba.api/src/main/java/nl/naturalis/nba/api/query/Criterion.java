package nl.naturalis.nba.api.query;

import java.util.List;

public class Criterion {

	private String field;
	private Operator operator;
	private Object value;
	private List<Criterion> or;
	private List<Criterion> and;

	public Criterion()
	{
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

	public List<Criterion> getOr()
	{
		return or;
	}

	public void setOr(List<Criterion> or)
	{
		this.or = or;
	}

	public List<Criterion> getAnd()
	{
		return and;
	}

	public void setAnd(List<Criterion> and)
	{
		this.and = and;
	}

}
