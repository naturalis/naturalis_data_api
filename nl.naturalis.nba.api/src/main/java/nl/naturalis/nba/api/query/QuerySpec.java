package nl.naturalis.nba.api.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a query specification. All information required by the various
 * {@code query} methods in the API take there input from a {@link QuerySpec}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public class QuerySpec {

	private List<Condition> conditions;
	private LogicalOperator logicalOperator;
	private List<SortField> sortFields;
	private int from;
	private int size;

	public QuerySpec()
	{
	}

	/**
	 * Adds the specified query condition to this {@code QuerySpec} instance.
	 * 
	 * @param condition
	 */
	public void addCondition(Condition condition)
	{
		if (conditions == null) {
			conditions = new ArrayList<>(5);
		}
		conditions.add(condition);
	}

	public void sortAcending(String field)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		SortField so = new SortField();
		so.setPath(field);
		sortFields.add(so);
	}

	public void sortDescending(String field)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		SortField so = new SortField();
		so.setPath(field);
		so.setAscending(false);
		sortFields.add(so);
	}

	public List<Condition> getConditions()
	{
		return conditions;
	}

	public void setConditions(List<Condition> conditions)
	{
		this.conditions = conditions;
	}

	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator == null ? AND : logicalOperator;
	}

	public void setLogicalOperator(LogicalOperator operator)
	{
		this.logicalOperator = operator;
	}

	public List<SortField> getSortFields()
	{
		return sortFields;
	}

	public void setSortFields(List<SortField> sortFields)
	{
		this.sortFields = sortFields;
	}

	public int getFrom()
	{
		return from;
	}

	public void setFrom(int from)
	{
		this.from = from;
	}

	public int getSize()
	{
		return size == 0 ? 10 : size;
	}

	public void setSize(int size)
	{
		this.size = size;
	}

}
