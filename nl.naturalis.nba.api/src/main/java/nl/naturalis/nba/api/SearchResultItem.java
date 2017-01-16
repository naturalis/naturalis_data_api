package nl.naturalis.nba.api;

public class SearchResultItem<T> {

	private T item;
	private float score;

	public SearchResultItem(T item, float score)
	{
		this.item = item;
		this.score = score;
	}

	public T getItem()
	{
		return item;
	}

	public void setItem(T item)
	{
		this.item = item;
	}

	public float getScore()
	{
		return score;
	}

	public void setScore(float score)
	{
		this.score = score;
	}

}
