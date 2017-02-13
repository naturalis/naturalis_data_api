package nl.naturalis.nba.api;

import static nl.naturalis.nba.api.SortOrder.ASC;
import static nl.naturalis.nba.api.SortOrder.DESC;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a field that you want a set of documents to be sorted on. See
 * {@link QuerySpec#setSortFields(java.util.List) QuerySpec.setSortFields}.
 * 
 * @author Ayco Holleman
 *
 */
public class SortField {

	private Path path;
	private SortOrder sortOrder;

	public SortField(String path)
	{
		this(path, ASC);
	}

	public SortField(Path path)
	{
		this(path, ASC);
	}

	public SortField(String path, SortOrder sortOrder)
	{
		this(new Path(path), sortOrder);
	}

	@JsonCreator
	public SortField(@JsonProperty("path") Path path,
			@JsonProperty("sortOrder") SortOrder sortOrder)
	{
		this.path = path;
		this.sortOrder = sortOrder;
	}

	/**
	 * Returns the path of the field on which to sort.
	 * 
	 * @return
	 */
	public Path getPath()
	{
		return path;
	}

	/**
	 * Returns the sort order (ascending or descending).
	 */
	public SortOrder getSortOrder()
	{
		return sortOrder;
	}

	public boolean isAscending()
	{
		return sortOrder != DESC; // null or ASC
	}

}
