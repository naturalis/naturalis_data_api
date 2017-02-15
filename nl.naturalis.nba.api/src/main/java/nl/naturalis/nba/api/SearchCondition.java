package nl.naturalis.nba.api;

import java.util.ArrayList;

public class SearchCondition extends AbstractSearchCondition<SearchCondition> {

	private Path field;
	private boolean constantScore;
	private float boost = 1F;

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
	public SearchCondition(String field, ComparisonOperator operator, Object value)
	{
		this(null, field, operator, value);
	}

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

	public Path getField()
	{
		return field;
	}

	public void setField(Path fields)
	{
		this.field = fields;
	}

	public void setFields(String fields)
	{
		this.field = new Path(fields);
	}

	public boolean isConstantScore()
	{
		return constantScore;
	}

	public void setConstantScore(boolean constantScore)
	{
		this.constantScore = constantScore;
	}

	public float getBoost()
	{
		return boost;
	}

	public void setBoost(float boost)
	{
		this.boost = boost;
	}

}
