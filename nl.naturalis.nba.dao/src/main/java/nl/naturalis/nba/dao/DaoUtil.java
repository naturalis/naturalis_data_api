package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;

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
	 * Creates a copy of the specified {@link SearchSpec} with conditions that
	 * only contain reasonably-sized {@link SearchCondition#getValue() search terms}.
	 * Some type of search terms (especially GeoJSON strings for complicated
	 * shapes) can easily become so large that even printing them as part of a
	 * DEBUG message is too expensive. This method is meant for debug purposes
	 * only, notably when passing a {@code SearchSpec} object to
	 * {@code logger.debug()}.
	 * 
	 * @param qs
	 * @return
	 */
	public static SearchSpec prune(SearchSpec qs)
	{
		if (qs == null || qs.getConditions() == null || qs.getConditions().size() == 0) {
			return qs;
		}
		SearchSpec copy = new SearchSpec();
		copy.setFields(qs.getFields());
		copy.setFrom(qs.getFrom());
		copy.setSize(qs.getSize());
		copy.setLogicalOperator(qs.getLogicalOperator());
		copy.setSortFields(qs.getSortFields());
		List<SearchCondition> siblings = new ArrayList<>(qs.getConditions().size());
		copy.setConditions(siblings);
		for (SearchCondition c : qs.getConditions()) {
			prune(c, siblings);
		}
		return copy;
	}

	/**
	 * Creates a copy of the specified conditions such that the search terms in
	 * the copies are reasonably-sized. See {@link #prune(SearchSpec)}.
	 * 
	 * @param conditions
	 * @return
	 */
	public static SearchCondition[] prune(SearchCondition[] conditions)
	{
		if (conditions == null || conditions.length == 0) {
			return conditions;
		}
		List<SearchCondition> copies = new ArrayList<>(conditions.length);
		for (SearchCondition c : conditions) {
			prune(c, copies);
		}
		return copies.toArray(new SearchCondition[copies.size()]);
	}

	private static void prune(SearchCondition condition, List<SearchCondition> siblings)
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

	private static SearchCondition createCopy(SearchCondition original, String newValue)
	{
		logger.debug("Value of field {} truncated or nullified for log file!", original.getField());
		SearchCondition copy = new SearchCondition();
		copy.setNot(original.getNot());
		copy.setField(original.getField());
		copy.setOperator(original.getOperator());
		copy.setValue(newValue);
		if (original.getAnd() != null) {
			List<SearchCondition> andCopy = new ArrayList<>(original.getAnd().size());
			copy.setAnd(andCopy);
			for (SearchCondition c : original.getAnd()) {
				prune(c, andCopy);
			}
		}
		if (original.getOr() != null) {
			List<SearchCondition> orCopy = new ArrayList<>(original.getOr().size());
			copy.setOr(orCopy);
			for (SearchCondition c : original.getOr()) {
				prune(c, orCopy);
			}
		}
		return copy;
	}
}
