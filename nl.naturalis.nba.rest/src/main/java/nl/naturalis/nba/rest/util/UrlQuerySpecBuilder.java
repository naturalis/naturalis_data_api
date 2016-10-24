package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.common.json.JsonUtil.deserialize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.CollectionUtil;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.LogicalOperator;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.api.query.SortField;
import nl.naturalis.nba.rest.exception.HTTP400Exception;

/**
 * Extracts {@link QuerySpec} objects from request URLs.
 * 
 * @author Ayco Holleman
 *
 */
public class UrlQuerySpecBuilder {

	public static final String PARAM_QUERY_SPEC = "_querySpec";
	public static final String PARAM_FIELDS = "_fields";
	public static final String PARAM_FROM = "_from";
	public static final String PARAM_SIZE = "_size";
	public static final String PARAM_OPERATOR = "_operator";
	public static final String PARAM_SORT_FIELDS = "_sortFields";

	private static final String ERR_ILLEGAL_PARAM = "Unknown or illegal parameter: %s";
	private static final String ERR_NO_UNDERSCORE = "Unknown or illegal parameter: "
			+ "querySpec. Did you mean _querySpec?";
	private static final String ERR_DUPLICATE_PARAM = "Duplicate parameter not allowed: %s";
	private static final String ERR_BAD_PARAM = "Invalid value for parameter %s: \"%s\"";
	private static final String ERR_BAD_INT_PARAM = "Parameter %s must be an integer (was \"%s\")";
	private static final String ERR_SORT_PARAM = "Parameter %s: sort order must be \"ASC\" or \"DESC\"";
	private static final String ERR_BAD_PARAM_COMBI = "Parameter _querySpec cannot be combined with %s";

	private static final Logger logger = LogManager.getLogger(UrlQuerySpecBuilder.class);

	private UriInfo uriInfo;

	public UrlQuerySpecBuilder(UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
	}

	public QuerySpec build()
	{
		logger.info("Extracting QuerySpec object from request URL");
		checkParams(uriInfo);
		MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
		List<String> values = params.get(PARAM_QUERY_SPEC);
		if (values != null) {
			if (values.size() != 1) {
				String msg = String.format(ERR_DUPLICATE_PARAM, PARAM_QUERY_SPEC);
				throw new HTTP400Exception(uriInfo, msg);
			}
			String json = values.iterator().next().trim();
			if (json.isEmpty()) {
				String msg = String.format(ERR_BAD_PARAM, PARAM_QUERY_SPEC, json);
				throw new HTTP400Exception(uriInfo, msg);
			}
			return deserialize(json, QuerySpec.class);
		}
		QuerySpec qs = new QuerySpec();
		logger.info("Parameter \"_querySpec\" not present in request URL. Assembling "
				+ "QuerySpec object from other query parameters in URL.");
		for (String param : params.keySet()) {
			values = params.get(param);
			if (values.size() != 1) {
				String msg = String.format(ERR_DUPLICATE_PARAM, param);
				throw new HTTP400Exception(uriInfo, msg);
			}
			String value = values.iterator().next();
			switch (param) {
				case "querySpec":
					throw new HTTP400Exception(uriInfo, ERR_NO_UNDERSCORE);
				case PARAM_SORT_FIELDS:
					qs.setSortFields(getSortFields(uriInfo, param, value));
					break;
				case PARAM_FROM:
					qs.setFrom(getIntParam(uriInfo, param, value));
					break;
				case PARAM_SIZE:
					qs.setSize(getIntParam(uriInfo, param, value));
					break;
				case PARAM_OPERATOR:
					qs.setLogicalOperator(getLogicalOperator(uriInfo, param, value));
					break;
				case PARAM_FIELDS:
					qs.setFields(getFields(value));
					break;
				default:
					if (param.charAt(0) == '_') {
						String msg = String.format(ERR_ILLEGAL_PARAM, param);
						throw new HTTP400Exception(uriInfo, msg);
					}
					qs.addCondition(new Condition(param, EQUALS, value));
					break;
			}
		}
		return qs;

	}

	private static void checkParams(UriInfo uriInfo)
	{
		MultivaluedMap<String, String> params = uriInfo.getQueryParameters();
		if (params.containsKey(PARAM_QUERY_SPEC)) {
			List<String> forbidden = Arrays.asList(PARAM_FIELDS, PARAM_FROM, PARAM_SIZE,
					PARAM_SORT_FIELDS, PARAM_OPERATOR);
			if (forbidden.removeAll(params.keySet())) {
				String imploded = CollectionUtil.implode(forbidden);
				String msg = String.format(ERR_BAD_PARAM_COMBI, imploded);
				throw new HTTP400Exception(uriInfo, msg);
			}
		}
	}

	private static int getIntParam(UriInfo uriInfo, String param, String value)
	{
		if (value.length() != 0) {
			return 0;
		}
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_BAD_INT_PARAM, param, value);
			throw new HTTP400Exception(uriInfo, msg);
		}
	}

	private static LogicalOperator getLogicalOperator(UriInfo uriInfo, String param, String value)
	{
		if (value.length() == 0) {
			return null;
		}
		LogicalOperator op;
		try {
			op = LogicalOperator.parse(value);
		}
		catch (IllegalArgumentException e) {
			String msg = String.format(ERR_BAD_PARAM, param, value);
			throw new HTTP400Exception(uriInfo, msg);
		}
		return op;
	}

	private static List<SortField> getSortFields(UriInfo uriInfo, String param, String value)
	{
		if (value.length() == 0) {
			return null;
		}
		String[] chunks = value.split(",");
		List<SortField> sortFields = new ArrayList<>(chunks.length);
		for (String chunk : chunks) {
			int i = chunk.indexOf(':');
			if (i == -1) {
				sortFields.add(new SortField(chunk));
			}
			else {
				String path = chunk.substring(0, i).trim();
				if (i == chunk.length() - 1) {
					sortFields.add(new SortField(path));
				}
				else {
					String order = chunk.substring(i + 1).trim().toUpperCase();
					if (order.equals("ASC") || order.equals("TRUE")) {
						sortFields.add(new SortField(path));
					}
					else if (order.equals("DESC") || order.equals("FALSE")) {
						sortFields.add(new SortField(path, false));
					}
					else {
						String msg = String.format(ERR_SORT_PARAM, param, value);
						throw new HTTP400Exception(uriInfo, msg);
					}
				}
			}
		}
		return sortFields;
	}

	private static List<String> getFields(String value)
	{
		if (value.length() == 0) {
			return null;
		}
		String[] chunks = value.split(",");
		List<String> fields = new ArrayList<>(chunks.length);
		for (String chunk : chunks) {
			fields.add(chunk.trim());
		}
		return fields;
	}

}
