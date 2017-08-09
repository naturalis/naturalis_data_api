package nl.naturalis.nba.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.naturalis.nba.api.model.IDocumentObject;

/**
 * A {@code QueryResultItem} contains a document (or other type of object)
 * returned from a {@link QuerySpec query}. Besides the document itself a
 * {@code QueryResultItem} also contains query-related metadata about the
 * document, for example a relevance score.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object coming back from the query. Note that while
 *            this will often be an instance of {@link IDocumentObject}, this is
 *            not required by the {@code QueryResultItem} class.
 */
@JsonPropertyOrder({ "score", "item" })
public class QueryResultItem<T> {

	private Float score;
	private T item;

	@JsonCreator
	public QueryResultItem(@JsonProperty("item") T item, @JsonProperty("score") float score)
	{
		this.item = item;
		/*
		 * When sorting on a field (rather than on relevance), Elasticsearch
		 * returns NaN for hit.getScore()
		 */
		if (!Float.isNaN(score)) {
			this.score = score;
		}
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
	 * Returns the relevance of the document returned from the query. Note that
	 * when sorting on a field (rather than on relevance), this method will
	 * return {@code null}.
	 * 
	 * @return
	 */
	public Float getScore()
	{
		return score;
	}

	/**
	 * Sets the relevance of the document returned from the query.
	 * 
	 * @param score
	 */
	public void setScore(Float score)
	{
		this.score = score;
	}

}
