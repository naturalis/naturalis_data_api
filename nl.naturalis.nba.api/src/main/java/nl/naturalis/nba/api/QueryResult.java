package nl.naturalis.nba.api;

import java.util.Iterator;
import java.util.List;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * A {@code QueryResult} represents the result from a {@link QuerySpec search
 * request}.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object returned by the search request. This can be a
 *            plain, unmodified Elasticsearch document (i.e. an implementation
 *            of {@link IDocumentObject}), but that is required by this class.
 */
public class QueryResult<T> implements Iterable<QueryResultItem<T>> {

	private long totalSize;
	private List<QueryResultItem<T>> resultSet;

	@Override
	public Iterator<QueryResultItem<T>> iterator()
	{
		return resultSet.iterator();
	}

	/**
	 * Returns the number of documents in this {@code QueryResult}.
	 * 
	 * @return
	 */
	public int size()
	{
		return resultSet.size();
	}

	/**
	 * Returns the document with the specified index.
	 * 
	 * @param index
	 * @return
	 */
	public QueryResultItem<T> get(int index)
	{
		return resultSet.get(index);
	}

	/**
	 * Returns the total number of documents conforming to the {@link QuerySpec
	 * query specification} that produced this query result.
	 * 
	 * @return
	 */
	public long getTotalSize()
	{
		return totalSize;
	}

	/**
	 * Sets the total number of documents conforming to the {@link QuerySpec
	 * query specification} that produced this query result. Not meant to be
	 * called by clients.
	 * 
	 * @param totalSize
	 */
	public void setTotalSize(long totalSize)
	{
		this.totalSize = totalSize;
	}

	/**
	 * Sets the result set of this {@code QueryResult}. Not meant to be called
	 * by clients.
	 * 
	 * @param items
	 */
	public void setResultSet(List<QueryResultItem<T>> items)
	{
		this.resultSet = items;
	}
}
