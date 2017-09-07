package nl.naturalis.nba.api;

import java.util.Arrays;

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
	 * Copy constructor. Currently only used within copy constructor for
	 * GroupByScientificNameQuerySpec, so package private.
	 */
	Filter(Filter other)
	{
		acceptRegexp = other.acceptRegexp;
		rejectRegexp = other.rejectRegexp;
		acceptValues = Arrays.copyOf(other.acceptValues, other.acceptValues.length);
		rejectValues = Arrays.copyOf(other.rejectValues, other.rejectValues.length);
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
		if (acceptRegexp == null) {
			if (other.acceptRegexp != null) {
				return false;
			}
		}
		else if (other.acceptRegexp == null) {
			return false;
		}
		else if (!acceptRegexp.equals(other.acceptRegexp)) {
			return false;
		}
		if (rejectRegexp == null) {
			if (other.rejectRegexp != null) {
				return false;
			}
		}
		else if (other.rejectRegexp == null) {
			return false;
		}
		else if (!rejectRegexp.equals(other.rejectRegexp)) {
			return false;
		}
		if (acceptValues == null) {
			if (other.acceptValues != null) {
				return false;
			}
		}
		else if (other.acceptValues == null) {
			return false;
		}
		else if (!Arrays.deepEquals(acceptValues, other.acceptValues)) {
			return false;
		}
		if (rejectValues == null) {
			if (other.rejectValues != null) {
				return false;
			}
		}
		else if (other.rejectValues == null) {
			return false;
		}
		else if (!Arrays.deepEquals(rejectValues, other.rejectValues)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + (acceptRegexp == null ? 0 : acceptRegexp.hashCode());
		hash = (hash * 31) + (rejectRegexp == null ? 0 : rejectRegexp.hashCode());
		hash = (hash * 31) + (acceptValues == null ? 0 : Arrays.deepHashCode(acceptValues));
		hash = (hash * 31) + (rejectValues == null ? 0 : Arrays.deepHashCode(rejectValues));
		return hash;
	}

}
