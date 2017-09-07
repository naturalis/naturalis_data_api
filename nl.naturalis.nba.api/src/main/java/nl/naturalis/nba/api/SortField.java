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

	/**
	 * The path to use if you want to sort documents by score: &#34;_score&#34;.
	 */
	public static final Path SORT_FIELD_SCORE = new Path("_score");

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

	/*
	 * Copy constructor. Only used within copy constructor for QuerySpec, so
	 * package private.
	 */
	SortField(SortField other)
	{
		path = other.path;
		sortOrder = other.sortOrder;
	}

	@JsonCreator
	public SortField(@JsonProperty("path") Path path,
			@JsonProperty("sortOrder") SortOrder sortOrder)
	{
		this.path = path;
		this.sortOrder = sortOrder == null ? ASC : sortOrder;
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

	/**
	 * Whether or not to sort in ascending order. Equivalent to
	 * {@code getSortOrder() == SortOrder.ASC}.
	 * 
	 * @return
	 */
	public boolean isAscending()
	{
		return sortOrder != DESC;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof SortField)) {
			return false;
		}
		SortField other = (SortField) obj;
		return path.equals(other.path) && ApiUtil.equals(sortOrder, other.sortOrder, ASC);
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + path.hashCode();
		hash = (hash * 31) + ApiUtil.hashCode(sortOrder, ASC);
		return hash;
	}

	@Override
	public String toString()
	{
		return path + ":" + sortOrder;
	}

}
