package nl.naturalis.nba.api.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.ITaxonAccess;

/**
 * Models an NBA query. All information required by the various {@code query}
 * methods in the API take a {@link QuerySpec} object to drive the query
 * process.
 * 
 * @author Ayco Holleman
 *
 */
public class QuerySpec {

	private List<String> fields;
	private List<Condition> conditions;
	private LogicalOperator logicalOperator;
	private List<SortField> sortFields;
	private int from;
	private int size;

	/**
	 * Creates a new, empty {@code QuerySpec} object.
	 */
	public QuerySpec()
	{
	}

	/**
	 * Specifies one or more fields to be returned in the query response. The
	 * field must belong to the document type being queried. See
	 * {@link #getFields() getFields}.
	 * 
	 * @param fields
	 */
	public void addFields(String... fields)
	{
		if (this.fields == null) {
			this.fields = new ArrayList<>();
		}
		this.fields.addAll(Arrays.asList(fields));
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

	/**
	 * Causes the documents in the result set to be sorted in ascending order of
	 * the specified field.
	 * 
	 * @param field
	 */
	public void sortBy(String field)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		sortFields.add(new SortField(field));
	}

	/**
	 * Causes the documents in the result set to be sorted on the specified
	 * field with the sort order determined by the {@code ascending} parameter.
	 * 
	 * @param field
	 */
	public void sortBy(String field, boolean ascending)
	{
		if (sortFields == null) {
			sortFields = new ArrayList<>(2);
		}
		sortFields.add(new SortField(field, ascending));
	}

	/**
	 * Returns the fields to return in the query response. This is akin to a SQL
	 * SELECT clause. By default all fields will be selected.
	 * 
	 * @return
	 */
	public List<String> getFields()
	{
		return fields;
	}

	/**
	 * Sets the fields to return in the query response. This is akin to a SQL
	 * SELECT clause. By default all fields will be selected. Note that if you
	 * use this {@code QuerySpec} instance to retrieve data model objects, for
	 * example as with {@link ISpecimenAccess#query(QuerySpec)
	 * ISpecimenAccess.query}, you <i>still</i> get those objects, only with all
	 * non-selected fields set to their default value ({@code null} for string
	 * fields and objects, zero for number fields and {@code false} for boolean
	 * fields). This might save some bandwidth, but it is probably marginal.
	 * However, limiting the number of fields you select <i>will</i> make a
	 * difference with methods like {@link ITaxonAccess#queryValues(QuerySpec)
	 * ITaxonAccess.queryValues}. In fact with this family of methods you
	 * <i>must</i> call {@code setFields} to specify the fields you want to show
	 * up in the result set.
	 * 
	 * @return
	 */
	public void setFields(List<String> fields)
	{
		this.fields = fields;
	}

	/**
	 * Returns the conditions a&#46;k&#46;a&#46; criteria that the documents
	 * must satify.
	 * 
	 * @return
	 */
	public List<Condition> getConditions()
	{
		return conditions;
	}

	/**
	 * Sets the conditions a&#46;k&#46;a&#46; criteria that the documents must
	 * satify.
	 * 
	 * @param conditions
	 */
	public void setConditions(List<Condition> conditions)
	{
		this.conditions = conditions;
	}

	/**
	 * Returns the logical operator ({@link LogicalOperator#AND AND} or
	 * {@link LogicalOperator#AND OR}) with which to join the
	 * {@link #getConditions() query conditions}. Note that any condition may
	 * itself contain a list of sibling AND and sibling OR conditions.
	 * 
	 * @return
	 */
	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator == null ? AND : logicalOperator;
	}

	/**
	 * Sets the logical operator ({@link LogicalOperator#AND AND} or
	 * {@link LogicalOperator#AND OR}) with which to join the
	 * {@link #getConditions() query conditions}. Note that any condition may
	 * itself contain a list of sibling AND and sibling OR conditions.
	 * 
	 * @param operator
	 */
	public void setLogicalOperator(LogicalOperator operator)
	{
		this.logicalOperator = operator;
	}

	/**
	 * Returns the fields on which to sort the documents in the result set.
	 * 
	 * @return
	 */
	public List<SortField> getSortFields()
	{
		return sortFields;
	}

	/**
	 * Sets the fields on which to sort the documents in the result set.
	 * 
	 * @param sortFields
	 */
	public void setSortFields(List<SortField> sortFields)
	{
		this.sortFields = sortFields;
	}

	/**
	 * Returns the offset in the total result set of documents satisfying the
	 * conditions of this {@code QuerySpec}.
	 * 
	 * @return
	 */
	public int getFrom()
	{
		return from;
	}

	/**
	 * Sets the offset in the total result set of documents satisfying the
	 * conditions of this {@code QuerySpec}.
	 * 
	 * @return
	 */
	public void setFrom(int from)
	{
		this.from = from;
	}

	/**
	 * Returns the number of documents to return.
	 * 
	 * @return
	 */
	public int getSize()
	{
		return size == 0 ? 10 : size;
	}

	/**
	 * Sets the number of documents to return.
	 * 
	 * @param size
	 */
	public void setSize(int size)
	{
		this.size = size;
	}

}
