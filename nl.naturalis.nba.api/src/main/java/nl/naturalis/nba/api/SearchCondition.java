package nl.naturalis.nba.api;

import java.util.ArrayList;
import java.util.List;

public class SearchCondition extends AbstractSearchCondition<SearchCondition> {

	private List<Path> fields;
	private boolean filter;
	private float boost;

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
		fields = other.fields;
		operator = other.operator;
		value = other.value;
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
		this.fields = new ArrayList<>(8);
		fields.add(field);
		this.operator = operator;
		this.value = value;
	}

	public SearchCondition addSearchField(String field)
	{
		if (fields == null) {
			fields = new ArrayList<>(8);
		}
		fields.add(new Path(field));
		return this;
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

	public List<Path> getFields()
	{
		return fields;
	}

	public void setFields(List<Path> fields)
	{
		this.fields = fields;
	}

	public boolean isFilter()
	{
		return filter;
	}

	public void setFilter(boolean filter)
	{
		this.filter = filter;
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
