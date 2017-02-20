package nl.naturalis.nba.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A {@code QueryResultItem} encapsulates a single document (or other type of
 * object) returned from a {@link QuerySpec query}.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object coming back from the query.
 */
public class QueryResultItem<T> {

	private T item;
	private float score;

	@JsonCreator
	public QueryResultItem(@JsonProperty("item") T item, @JsonProperty("score") float score)
	{
		this.item = item;
		this.score = score;
	}

	/**
	 * Returns the document (or other type of object) returned from a query.
	 * 
	 * @return
	 */
	public T getItem()
	{
		return item;
	}

	/**
	 * Returns the relevance of the document returned from the query.
	 * 
	 * @return
	 */
	public float getScore()
	{
		return score;
	}

}
