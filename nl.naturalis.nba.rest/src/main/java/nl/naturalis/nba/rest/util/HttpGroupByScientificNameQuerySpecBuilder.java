package nl.naturalis.nba.rest.util;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.SortOrder.ASC;
import static nl.naturalis.nba.api.SortOrder.DESC;
import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.utils.ConfigObject.isTrueValue;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.Filter;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec.GroupSort;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.rest.exception.HTTP400Exception;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * Extracts {@link GroupByScientificNameQuerySpec} objects from HTTP requests.
 * 
 * @see GroupByScientificNameQuerySpec
 * 
 * @author Ayco Holleman
 *
 */
public class HttpGroupByScientificNameQuerySpecBuilder {

	public static final String PARAM_QUERY_SPEC = "_querySpec";
	public static final String PARAM_FIELDS = "_fields";
	public static final String PARAM_FROM = "_from";
	public static final String PARAM_SIZE = "_size";
	public static final String PARAM_OPERATOR = "_logicalOperator";
	public static final String PARAM_SORT_FIELDS = "_sortFields";
	public static final String PARAM_IGNORE_CASE = "_ignoreCase";
	public static final String PARAM_GROUP_SORT = "_groupSort";
	public static final String PARAM_GROUP_FILTER = "_groupFilter";
	public static final String PARAM_SPECIMENS_FROM = "_specimensFrom";
	public static final String PARAM_SPECIMENS_SIZE = "_specimensSize";
	public static final String PARAM_SPECIMENS_SORT_FIELDS = "_specimensSortFields";
	public static final String PARAM_SPECIMENS_NO_TAXA = "_noTaxa";

	private static final String ERR_ILLEGAL_PARAM = "Unknown or illegal parameter: %s";
	private static final String ERR_NO_UNDERSCORE = "Unknown or illegal parameter: "
			+ "querySpec. Did you mean _querySpec?";
	private static final String ERR_DUPLICATE_PARAM = "Duplicate parameter not allowed: %s";
	private static final String ERR_BAD_PARAM = "Invalid value for parameter %s: \"%s\"";
	private static final String ERR_BAD_INT_PARAM = "Parameter %s must be an integer (was \"%s\")";
	private static final String ERR_SORT_PARAM = "Parameter %s: sort order must be \"ASC\" or \"DESC\"";
	//private static final String ERR_BAD_PARAM_COMBI = "Parameter _querySpec cannot be combined with %s";
	private static final String ERR_BAD_PARAM_COMBI = "Parameter _querySpec cannot be combined with any other parameter.";

	private static final Logger logger = LogManager.getLogger(HttpGroupByScientificNameQuerySpecBuilder.class);

	private UriInfo uriInfo;
	private MultivaluedMap<String, String> params;

	/**
	 * Creates a {@link GroupByScientificNameQuerySpec} from the query
	 * parameters present in the URL.
	 * 
	 * @param uriInfo
	 */
	public HttpGroupByScientificNameQuerySpecBuilder(UriInfo uriInfo)
	{
		this.uriInfo = uriInfo;
		this.params = uriInfo.getQueryParameters();
	}

	/**
	 * Creates a {@link GroupByScientificNameQuerySpec} from the form data in a
	 * x-www-form-urlencoded request body.
	 * 
	 * @param formData
	 * @param uriInfo
	 */
	public HttpGroupByScientificNameQuerySpecBuilder(MultivaluedMap<String, String> formData,
			UriInfo uriInfo)
	{
		this.params = formData;
		this.uriInfo = uriInfo;
	}

	public GroupByScientificNameQuerySpec build()
	{
		logger.info("Extracting NameGroupQuerySpec object from request");
		checkParams(uriInfo);
		List<String> values = params.get(PARAM_QUERY_SPEC);
		if (values != null) {
			return buildFromQuerySpecParam(values);
		}
		GroupByScientificNameQuerySpec qs = new GroupByScientificNameQuerySpec();
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
				case PARAM_OPERATOR:
					qs.setLogicalOperator(getLogicalOperator(value));
					break;
				case PARAM_FROM:
					qs.setFrom(getIntParam(param, value));
					break;
				case PARAM_SIZE:
					qs.setSize(getIntParam(param, value));
					break;
				case PARAM_SORT_FIELDS:
					qs.setSortFields(getSortFields(value));
					break;
				case PARAM_FIELDS:
					qs.setFields(getFields(value));
					break;
				case PARAM_GROUP_SORT:
					qs.setGroupSort(GroupSort.parse(value));
					break;
				case PARAM_GROUP_FILTER:
					if (value.length() != 0) {
						String[] filters = value.split(",");
						Filter filter = new Filter();
						if (filters.length == 1) {
							filter.acceptRegexp(filters[0]);
						}
						else {
							filter.acceptValues(filters);
						}
						qs.setGroupFilter(filter);
					}
					break;
				case PARAM_SPECIMENS_FROM:
					qs.setSpecimensFrom(getIntParam(param, value));
					break;
				case PARAM_SPECIMENS_SIZE:
					qs.setSpecimensSize(getIntParam(param, value));
					break;
				case PARAM_SPECIMENS_SORT_FIELDS:
					qs.setSpecimensSortFields(getSortFields(value));
					break;
				case PARAM_SPECIMENS_NO_TAXA:
					qs.setNoTaxa(ConfigObject.isTrueValue(value, true));
					break;
				default:
					if (param.charAt(0) == '_') {
						String msg = String.format(ERR_ILLEGAL_PARAM, param);
						throw new HTTP400Exception(uriInfo, msg);
					}
					if (value.equals("@NULL@")) {
						qs.addCondition(new QueryCondition(param, EQUALS, null));
					}
					else if (value.equals("@NOT_NULL@")) {
						qs.addCondition(new QueryCondition(param, NOT_EQUALS, null));
					}
					else {
						qs.addCondition(new QueryCondition(param, operator, value));
					}
					break;
			}
		}
		return qs;
	}

	private GroupByScientificNameQuerySpec buildFromQuerySpecParam(List<String> values)
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
		return deserialize(json, GroupByScientificNameQuerySpec.class);
	}

	private void checkParams(UriInfo uriInfo)
	{
		if (params.containsKey(PARAM_QUERY_SPEC) && params.size() > 1) {
			throw new HTTP400Exception(uriInfo, ERR_BAD_PARAM_COMBI);
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
