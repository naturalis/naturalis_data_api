package nl.naturalis.nba.api;

/**
 * A generic filter definition. Used by {@link GroupByScientificNameQuerySpec}
 * to filter the groups (a.k.a. buckets) returned from the GROUP BY aggregation.
 * 
 * @author Ayco Holleman
 *
 */
public class Filter {

	private String acceptRegexp;
	private String rejectRegexp;
	private String[] acceptValues;
	private String[] rejectValues;

	/**
	 * Returns the (literal) values to be accepted.
	 * 
	 * @return
	 */
	public String[] getAcceptValues()
	{
		return acceptValues;
	}

	/**
	 * Sets the (literal) values to be accepted.
	 * 
	 * @param values
	 */
	public void acceptValues(String[] values)
	{
		this.acceptValues = values;
	}

	/**
	 * Returns the (literal) values to be rejected.
	 * 
	 * @return
	 */
	public String[] getRejectValues()
	{
		return rejectValues;
	}

	/**
	 * Sets the (literal) values to be rejected.
	 * 
	 * @return
	 */
	public void rejectValues(String[] values)
	{
		this.rejectValues = values;
	}

	/**
	 * Returns the regular expression that values should match.
	 * 
	 * @return
	 */
	public String getAcceptRegexp()
	{
		return acceptRegexp;
	}

	/**
	 * Sets the regular expression that values should match.
	 * 
	 * @return
	 */
	public void acceptRegexp(String regexp)
	{
		this.acceptRegexp = regexp;
	}

	/**
	 * Returns the regular expression that values should <i>not</i> match.
	 * 
	 * @return
	 */
	public String getRejectRegexp()
	{
		return rejectRegexp;
	}

	/**
	 * Sets the regular expression that values should <i>not</i> match.
	 * 
	 * @return
	 */
	public void rejectRegexp(String regexp)
	{
		this.rejectRegexp = regexp;
	}

}
