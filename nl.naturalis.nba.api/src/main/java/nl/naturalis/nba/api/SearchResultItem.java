package nl.naturalis.nba.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A {@code SearchResultItem} encapsulates a single document (or other type of
 * object) returned from a {@link SearchSpec query}.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object coming back from the query.
 */
public class SearchResultItem<T> {

	private T item;
	private float score;

	@JsonCreator
	public SearchResultItem(@JsonProperty("item") T item, @JsonProperty("score") float score)
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
