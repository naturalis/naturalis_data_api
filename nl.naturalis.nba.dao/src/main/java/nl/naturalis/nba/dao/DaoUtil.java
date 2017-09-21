package nl.naturalis.nba.dao;

import static nl.naturalis.nba.utils.StringUtil.zpad;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.search.aggregations.bucket.terms.support.IncludeExclude;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;

/**
 * Utility class providing common functionality for classes in the dao package.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoUtil {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(DaoUtil.class);

	private DaoUtil()
	{
	}

	/**
	 * Returns a log4j logger for the specified class.
	 * 
	 * @param cls
	 * @return
	 */
	public static Logger getLogger(Class<?> cls)
	{
		return DaoRegistry.getInstance().getLogger(cls);
	}

	/**
	 * Get the duration between {@code start} and now, formatted as HH:mm:ss.
	 * 
	 * @param start
	 * @return
	 */
	public static String getDuration(long start)
	{
		return getDuration(start, System.currentTimeMillis());
	}

	/**
	 * Get the duration between {@code start} and {@code end}, formatted as
	 * HH:mm:ss.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getDuration(long start, long end)
	{
		int millis = (int) (end - start);
		int hours = millis / (60 * 60 * 1000);
		millis = millis % (60 * 60 * 1000);
		int minutes = millis / (60 * 1000);
		millis = millis % (60 * 1000);
		int seconds = millis / 1000;
		return zpad(hours, 2, ":") + zpad(minutes, 2, ":") + zpad(seconds, 2);
	}

	/**
	 * Creates a copy of the specified {@link QuerySpec} with conditions that
	 * only contain reasonably-sized {@link QueryCondition#getValue() search
	 * terms}. Some type of search terms (especially GeoJSON strings for
	 * complicated shapes) can easily become so large that even printing them as
	 * part of a DEBUG message is expensive. This method is meant for debug
	 * purposes only, when passing a {@code QuerySpec} object to
	 * {@code logger.debug()}.
	 * 
	 * @param qs
	 * @return
	 */
	public static QuerySpec prune(QuerySpec qs)
	{
		if (qs == null || qs.getConditions() == null || qs.getConditions().size() == 0) {
			return qs;
		}
		QuerySpec copy = new QuerySpec();
		copy.setFields(qs.getFields());
		copy.setFrom(qs.getFrom());
		copy.setSize(qs.getSize());
		copy.setLogicalOperator(qs.getLogicalOperator());
		copy.setSortFields(qs.getSortFields());
		List<QueryCondition> siblings = new ArrayList<>(qs.getConditions().size());
		copy.setConditions(siblings);
		for (QueryCondition c : qs.getConditions()) {
			prune(c, siblings);
		}
		return copy;
	}

	/**
	 * Creates a copy of the specified conditions such that the search terms in
	 * the copies are reasonably-sized. See {@link #prune(QuerySpec)}.
	 * 
	 * @param conditions
	 * @return
	 */
	public static QueryCondition[] prune(QueryCondition[] conditions)
	{
		if (conditions == null || conditions.length == 0) {
			return conditions;
		}
		List<QueryCondition> copies = new ArrayList<>(conditions.length);
		for (QueryCondition c : conditions) {
			prune(c, copies);
		}
		return copies.toArray(new QueryCondition[copies.size()]);
	}

	static final String ERR_BAD_FILTER = "Accept filter and Reject filter must "
			+ "have the same type (regular expression or string array)";

	public static IncludeExclude translateFilter(Filter filter) throws InvalidQueryException
	{
		IncludeExclude ie = null;
		if (filter.getAcceptRegexp() != null || filter.getRejectRegexp() != null) {
			if (filter.getAcceptValues() != null || filter.getRejectValues() != null) {
				throw new InvalidQueryException(ERR_BAD_FILTER);
			}
			ie = new IncludeExclude(filter.getAcceptRegexp(), filter.getRejectRegexp());
		}
		else if (filter.getAcceptValues() != null || filter.getRejectValues() != null) {
			if (filter.getAcceptRegexp() != null || filter.getRejectRegexp() != null) {
				throw new InvalidQueryException(ERR_BAD_FILTER);
			}
			ie = new IncludeExclude(filter.getAcceptValues(), filter.getRejectValues());
		}
		return ie;
	}

	private static void prune(QueryCondition condition, List<QueryCondition> siblings)
	{
		Object val = condition.getValue();
		if (val == null) {
			siblings.add(condition);
		}
		else if (val instanceof CharSequence) {
			if (((CharSequence) val).length() < 500) {
				siblings.add(condition);
			}
			else {
				String s = val.toString().substring(0, 500) + "... <truncated>";
				siblings.add(createCopy(condition, s));
			}
		}
		else if (val instanceof GeoJsonObject) {
			siblings.add(createCopy(condition, null));
		}
		else {
			siblings.add(condition);
		}
	}

	private static QueryCondition createCopy(QueryCondition original, String newValue)
	{
		QueryCondition copy = new QueryCondition(original);
		copy.setValue(newValue);
		return copy;
	}

}
