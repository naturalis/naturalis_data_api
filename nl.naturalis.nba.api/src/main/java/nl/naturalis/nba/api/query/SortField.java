package nl.naturalis.nba.api.query;

/**
 * Represents a field that you want a set of documents to be sorted on. See
 * {@link QuerySpec#setSortFields(java.util.List) QuerySpec.setSortFields}.
 * 
 * @author Ayco Holleman
 *
 */
public class SortField {

	private String path;
	private boolean ascending;

	public SortField()
	{
		this(null, true);
	}

	public SortField(String path)
	{
		this(path, true);
	}

	public SortField(String path, boolean ascending)
	{
		this.path = path;
		this.ascending = ascending;
	}

	/**
	 * Returns the path of the field on which to sort.
	 * 
	 * @return
	 */
	public String getPath()
	{
		return path;
	}

	/**
	 * Sets the path of the field on which to sort.
	 * 
	 * @param path
	 */
	public void setPath(String path)
	{
		this.path = path;
	}

	/**
	 * Whether to sort in ascending order (default: {@code true}).
	 * 
	 * @return
	 */
	public boolean isAscending()
	{
		return ascending;
	}

	/**
	 * Sets the sort order.
	 * 
	 * @param ascending
	 */
	public void setAscending(boolean ascending)
	{
		this.ascending = ascending;
	}

}
