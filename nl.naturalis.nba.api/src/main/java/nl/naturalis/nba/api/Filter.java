package nl.naturalis.nba.api;

/**
 * A generic filter definition. Used by {@link GroupByScientificNameQuerySpec}
 * to filter the groups (a.k.a. buckets) returned from the GROUP BY aggregation.
 * 
 * @author Ayco Holleman
 *
 */
public class Filter {

	private Object accept;
	private Object reject;

	/**
	 * Accept the specified (literal) values.
	 * 
	 * @param values
	 */
	public void acceptValues(String[] values)
	{
		if (reject != null && reject.getClass() != values.getClass()) {
			throwIllegalArgumentException();
		}
		this.accept = values;
	}

	/**
	 * Accept all values conforming to the specified regular expression.
	 * 
	 * @param regexp
	 */
	public void acceptRegexp(String regexp)
	{
		if (reject != null && reject.getClass() != regexp.getClass()) {
			throwIllegalArgumentException();
		}
		this.accept = regexp;
	}

	/**
	 * Reject the specified (literal) values.
	 * 
	 * @param values
	 */
	public void rejectValues(String[] values)
	{
		if (accept != null && accept.getClass() != values.getClass()) {
			throwIllegalArgumentException();
		}
		this.reject = values;
	}

	/**
	 * Reject all values conforming to the specified regular expression.
	 * 
	 * @param regexp
	 */
	public void rejectRegexp(String regexp)
	{
		if (accept != null && accept.getClass() != regexp.getClass()) {
			throwIllegalArgumentException();
		}
		this.reject = regexp;
	}

	/**
	 * Returns the accepted values either a an array of literal values or as a
	 * single regular expression string.
	 * 
	 * @return
	 */
	public Object getAccept()
	{
		return accept;
	}

	/**
	 * Returns the rejected values either a an array of literal values or as a
	 * single regular expression string.
	 * 
	 * @return
	 */
	public Object getReject()
	{
		return reject;
	}

	private static void throwIllegalArgumentException()
	{
		throw new IllegalArgumentException("Accept filter and Reject filter must have same type");
	}

}
