package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;

/**
 * Utility class providing common functionality for classes in the dao package.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoUtil {

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
	 * Creates a copy of the specified {@link QuerySpec} with conditions that
	 * only contain reasonably-sized {@link QueryCondition#getValue() search terms}.
	 * Some type of search terms (especially GeoJSON strings for complicated
	 * shapes) can easily become so large that even printing them as part of a
	 * DEBUG message is too expensive. This method is meant for debug purposes
	 * only, notably when passing a {@code QuerySpec} object to
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
		logger.debug("Value of field {} truncated or nullified for log file!", original.getField());
		QueryCondition copy = new QueryCondition();
		copy.setNot(original.getNot());
		copy.setField(original.getField());
		copy.setOperator(original.getOperator());
		copy.setValue(newValue);
		if (original.getAnd() != null) {
			List<QueryCondition> andCopy = new ArrayList<>(original.getAnd().size());
			copy.setAnd(andCopy);
			for (QueryCondition c : original.getAnd()) {
				prune(c, andCopy);
			}
		}
		if (original.getOr() != null) {
			List<QueryCondition> orCopy = new ArrayList<>(original.getOr().size());
			copy.setOr(orCopy);
			for (QueryCondition c : original.getOr()) {
				prune(c, orCopy);
			}
		}
		return copy;
	}
}
