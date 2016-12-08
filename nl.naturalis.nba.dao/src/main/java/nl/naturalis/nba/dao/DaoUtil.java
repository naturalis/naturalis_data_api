package nl.naturalis.nba.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.QuerySpec;

public class DaoUtil {

	private static Logger logger = getLogger(DaoUtil.class);

	private DaoUtil()
	{
	}

	public static Logger getLogger(Class<?> cls)
	{
		return DaoRegistry.getInstance().getLogger(cls);
	}

	/**
	 * Creates a copy of the specified {@link QuerySpec} with conditions that
	 * only contain reasonably-sized {@link Condition#getValue() search terms}.
	 * Some type of search terms (especially GeoJSON strings for complicated
	 * shapes) can easily become so large that even printing them as part of a
	 * DEBUG message is too expensive. This method is only meant to be called
	 * when passing a {@code QuerySpec} object to {@code logger.debug()}. If the
	 * specified {@code QuerySpec} instance does not contain any conditions, no
	 * copy is made; the instance itself is returned.
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
		List<Condition> siblings = new ArrayList<>(qs.getConditions().size());
		copy.setConditions(siblings);
		for (Condition c : qs.getConditions()) {
			prune(c, siblings);
		}
		return copy;
	}

	public static Condition[] prune(Condition[] conditions)
	{
		if (conditions == null || conditions.length == 0) {
			return conditions;
		}
		List<Condition> copies = new ArrayList<>(conditions.length);
		for (Condition c : conditions) {
			prune(c, copies);
		}
		return copies.toArray(new Condition[copies.size()]);
	}

	private static void prune(Condition condition, List<Condition> siblings)
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

	private static Condition createCopy(Condition original, String newValue)
	{
		logger.debug("Value of field {} truncated or nullified in log file!", original.getField());
		Condition copy = new Condition();
		copy.setNot(original.getNot());
		copy.setField(original.getField());
		copy.setOperator(original.getOperator());
		copy.setValue(newValue);
		if (original.getAnd() != null) {
			List<Condition> andCopy = new ArrayList<>(original.getAnd().size());
			copy.setAnd(andCopy);
			for (Condition c : original.getAnd()) {
				prune(c, andCopy);
			}
		}
		if (original.getOr() != null) {
			List<Condition> orCopy = new ArrayList<>(original.getOr().size());
			copy.setOr(orCopy);
			for (Condition c : original.getOr()) {
				prune(c, orCopy);
			}
		}
		return copy;
	}
}
