package nl.naturalis.nba.api;

import java.util.Arrays;
import java.util.Objects;

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

	public Filter()
	{
	}

	/*
	 * Copy constructor. Only used within copy constructor for
	 * GroupByScientificNameQuerySpec, so package private.
	 */
	Filter(Filter other)
	{
		acceptRegexp = other.acceptRegexp;
		rejectRegexp = other.rejectRegexp;
		if (other.acceptValues != null) {
			acceptValues = Arrays.copyOf(other.acceptValues, other.acceptValues.length);
		}
		if (other.rejectValues != null) {
			rejectValues = Arrays.copyOf(other.rejectValues, other.rejectValues.length);
		}
	}

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

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Filter)) {
			return false;
		}
		Filter other = (Filter) obj;
		return Objects.equals(acceptRegexp, other.acceptRegexp)
				&& Objects.equals(rejectRegexp, other.rejectRegexp)
				&& Objects.deepEquals(acceptValues, other.acceptValues)
				&& Objects.deepEquals(rejectValues, other.rejectValues);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(acceptRegexp, rejectRegexp, acceptValues, rejectValues);
	}

}
