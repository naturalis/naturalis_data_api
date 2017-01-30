package nl.naturalis.nba.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;

public abstract class AbstractSearchSpec<T extends AbstractSearchCondition<T>> {

	private List<String> fields;
	private List<T> conditions;
	private LogicalOperator logicalOperator;
	private List<SortField> sortFields;
	private Integer from;
	private Integer size;

	public AbstractSearchSpec()
	{
		super();
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
	public void addCondition(T condition)
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
	 * field with the sort order determined by the {@code ascending} argument.
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
	 * SELECT clause. By default all fields will be selected. Be aware of the
	 * effect this method has when querying data model objects like {@link Taxon
	 * taxa} or {@link Specimen specimens} (e.g. with the
	 * {@link INbaAccess#query(SearchSpec) query} method): you <i>still</i> get
	 * back full-blown {@code Taxon} c.q. {@code Specimen} objects, only with
	 * all non-selected fields set to their default value ({@code null} for
	 * strings, dates and objects, zero for number fields and {@code false} for
	 * boolean fields). Thus, the value of a non-selected field has no relation
	 * to its actual value in the NBA data store. This is especially confusing
	 * if you also specified a {@link QueryCondition} for that field (e.g. you
	 * specified it to be {@code true} but in the query result it appears to be
	 * {@code false} - the default boolean value). Therefore: <i>do not read
	 * values of fields you did not select!</i>
	 * 
	 * @return
	 */
	public void setFields(List<String> fields)
	{
		this.fields = fields;
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
		return logicalOperator;
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
	 * conditions of this {@code QuerySpec}. Defaults to 0 (zero).
	 * 
	 * @return
	 */
	public Integer getFrom()
	{
		return from;
	}

	/**
	 * Sets the offset in the total result set of documents satisfying the
	 * conditions of this {@code QuerySpec}.
	 * 
	 * @return
	 */
	public void setFrom(Integer from)
	{
		this.from = from;
	}

	/**
	 * Returns the number of documents to return. Defaults to 10.
	 * 
	 * @return
	 */
	public Integer getSize()
	{
		return size;
	}

	/**
	 * Sets the number of documents to return.
	 * 
	 * @param size
	 */
	public void setSize(Integer size)
	{
		this.size = size;
	}

	/**
	 * Returns the conditions a&#46;k&#46;a&#46; criteria that the documents
	 * must satify.
	 * 
	 * @return
	 */
	public List<T> getConditions()
	{
		return conditions;
	}

	/**
	 * Sets the conditions a&#46;k&#46;a&#46; criteria that the documents must
	 * satify.
	 * 
	 * @param conditions
	 */
	public void setConditions(List<T> conditions)
	{
		this.conditions = conditions;
	}

}