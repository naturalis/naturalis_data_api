package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;

import java.util.ArrayList;
import java.util.List;

public class SearchCondition {

	private UnaryBooleanOperator not;
	private List<SearchField> fields;
	private ComparisonOperator operator;
	private Object value;
	private List<SearchCondition> and;
	private List<SearchCondition> or;

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

	public SearchCondition(SearchField field, ComparisonOperator operator, Object value)
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
		this(not, new SearchField(field), operator, value);
	}

	public SearchCondition(UnaryBooleanOperator not, SearchField field, ComparisonOperator operator,
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
		fields.add(new SearchField(field));
		return this;
	}

	public SearchCondition addSearchField(String field, float boost)
	{
		if (fields == null) {
			fields = new ArrayList<>(8);
		}
		fields.add(new SearchField(field, boost));
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

	// GETTERS & SETTERS:

	public UnaryBooleanOperator getNot()
	{
		return not;
	}

	public void setNot(UnaryBooleanOperator not)
	{
		this.not = not;
	}

	public List<SearchField> getFields()
	{
		return fields;
	}

	public void setFields(List<SearchField> fields)
	{
		this.fields = fields;
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

	public List<SearchCondition> getAnd()
	{
		return and;
	}

	public void setAnd(List<SearchCondition> and)
	{
		this.and = and;
	}

	public List<SearchCondition> getOr()
	{
		return or;
	}

	public void setOr(List<SearchCondition> or)
	{
		this.or = or;
	}

}
