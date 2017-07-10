package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.SortOrder.ASC;
import static nl.naturalis.nba.api.SortOrder.DESC;
import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.utils.ConfigObject.isTrueValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.rest.exception.HTTP400Exception;
import nl.naturalis.nba.utils.CollectionUtil;

/**
 * Extracts {@link QuerySpec} objects from HTTP requests.
 * 
 * @see QuerySpec
 * 
 * @author Ayco Holleman
 *
 */
public class HttpQuerySpecBuilder {

	public static final String PARAM_QUERY_SPEC = "_querySpec";
	public static final String PARAM_FIELDS = "_fields";
	public static final String PARAM_FROM = "_from";
	public static final String PARAM_SIZE = "_size";
	public static final String PARAM_OPERATOR = "_logicalOperator";
	public static final String PARAM_SORT_FIELDS = "_sortFields";
	public static final String PARAM_IGNORE_CASE = "_ignoreCase";

	private static final String ERR_ILLEGAL_PARAM = "Unknown or illegal parameter: %s";
	private static final String ERR_NO_UNDERSCORE = "Unknown or illegal parameter: "
			+ "querySpec. Did you mean _querySpec?";
	private static final String ERR_DUPLICATE_PARAM = "Duplicate parameter not allowed: %s";
	private static final String ERR_BAD_PARAM = "Invalid value for parameter %s: \"%s\"";
	private static final String ERR_BAD_INT_PARAM = "Parameter %s must be an integer (was \"%s\")";
	private static final String ERR_SORT_PARAM = "Parameter %s: sort order must be \"ASC\" or \"DESC\"";
	private static final String ERR_BAD_PARAM_COMBI = "Parameter _querySpec cannot be combined with %s";

	private static final Logger logger = LogManager.getLogger(HttpQuerySpecBuilder.class);

	private UriInfo uriInfo;
	private MultivaluedMap<String, String> params;

	/**
	 * Creates a {@link QuerySpec} from the query parameters present in the URL.
	 * 
	 * @param uriInfo
	 */
	public HttpQuerySpecBuilder(UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
		this.params = uriInfo.getQueryParameters();
	}

	/**
	 * Creates a {@link QuerySpec} from the form data in a x-www-form-urlencoded
	 * request body.
	 * 
	 * @param formData
	 * @param uriInfo
	 */
	public HttpQuerySpecBuilder(MultivaluedMap<String, String> formData, UriInfo uriInfo)
	{
		this.params = formData;
		this.uriInfo = uriInfo;
	}

	public QuerySpec build()
	{
		logger.info("Extracting QuerySpec object from request");
		checkParams(uriInfo);
		List<String> values = params.get(PARAM_QUERY_SPEC);
		if (values != null) {
			return buildFromSearchSpecParam(values);
		}
		QuerySpec qs = new QuerySpec();
		ComparisonOperator operator = getComparisonOperator();
		for (String param : params.keySet()) {
			values = params.get(param);
			if (values.size() != 1) {
				String msg = String.format(ERR_DUPLICATE_PARAM, param);
				throw new HTTP400Exception(uriInfo, msg);
			}
			String value = values.iterator().next();
			logger.info("Processing parameter {}: \"{}\"", param, value);
			switch (param) {
				case "querySpec":
					throw new HTTP400Exception(uriInfo, ERR_NO_UNDERSCORE);
				case PARAM_IGNORE_CASE:
					break;
				case PARAM_SORT_FIELDS:
					qs.setSortFields(getSortFields(value));
					break;
				case PARAM_FROM:
					qs.setFrom(getIntParam(param, value));
					break;
				case PARAM_SIZE:
					qs.setSize(getIntParam(param, value));
					break;
				case PARAM_OPERATOR:
					qs.setLogicalOperator(getLogicalOperator(value));
					break;
				case PARAM_FIELDS:
					qs.setFields(getFields(value));
					break;
				default:
					if (param.charAt(0) == '_') {
						String msg = String.format(ERR_ILLEGAL_PARAM, param);
						throw new HTTP400Exception(uriInfo, msg);
					}
					if (value.equals("@NULL@")) {
						value = null;
					}
					qs.addCondition(new QueryCondition(param, operator, value));
					break;
			}
		}
		return qs;
	}

	private QuerySpec buildFromSearchSpecParam(List<String> values)
	{
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

	private void checkParams(UriInfo uriInfo)
	{
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

	/*
	 * Returns the value of _from or _size query parameter.
	 */
	private Integer getIntParam(String param, String value)
	{
		if (value.length() == 0) {
			return null;
		}
		try {
			return Integer.valueOf(value);
		}
		catch (NumberFormatException e) {
			String msg = String.format(ERR_BAD_INT_PARAM, param, value);
			throw new HTTP400Exception(uriInfo, msg);
		}
	}

	/*
	 * Return EQUALS_IC if _ignoreCase query parameter is present and equal to
	 * "true", "1", "on", or "yes". Otherwise EQUALS.
	 */
	private ComparisonOperator getComparisonOperator()
	{
		String ignoreCase = params.getFirst(PARAM_IGNORE_CASE);
		if (isTrueValue(ignoreCase)) {
			return EQUALS_IC;
		}
		return EQUALS;
	}

	/*
	 * Returns value of _logicalOperator query parameter.
	 */
	private LogicalOperator getLogicalOperator(String value)
	{
		if (value.length() == 0) {
			return null;
		}
		LogicalOperator op;
		try {
			op = LogicalOperator.parse(value);
		}
		catch (IllegalArgumentException e) {
			String msg = String.format(ERR_BAD_PARAM, PARAM_OPERATOR, value);
			throw new HTTP400Exception(uriInfo, msg);
		}
		return op;
	}

	/*
	 * Returns value of _sortFields query parameter.
	 */
	private List<SortField> getSortFields(String value)
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
					if (order.equals("ASC")) {
						sortFields.add(new SortField(path, ASC));
					}
					else if (order.equals("DESC")) {
						sortFields.add(new SortField(path, DESC));
					}
					else {
						String msg = String.format(ERR_SORT_PARAM, PARAM_SORT_FIELDS, value);
						throw new HTTP400Exception(uriInfo, msg);
					}
				}
			}
		}
		return sortFields;
	}

	/*
	 * Returns value of _fields query parameter.
	 */
	private static List<Path> getFields(String value)
	{
		if (value.length() == 0) {
			return null;
		}
		String[] chunks = value.split(",");
		List<Path> fields = new ArrayList<>(chunks.length);
		for (String chunk : chunks) {
			fields.add(new Path(chunk.trim()));
		}
		return fields;
	}

}
