package nl.naturalis.nba.dao.format.filter;

import java.util.Map;

import org.domainobject.util.ConfigObject;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.EntityFilterException;
import nl.naturalis.nba.dao.format.EntityFilterInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;

/**
 * A generic entity filter that will suit for many use cases. The {@link Map}
 * passed to the {@link #initialize(Map) initialize} method may contain the
 * following arguments:
 * <ol>
 * <li><b>path</b> Required. Specifies the path of the field containing the
 * values to filter on.
 * <li><b>values</b> Required. A comma-separated list of values to filter on. If
 * the field has any of the values in the list, the entity will <b>not</b> be
 * written to the data set. You may provide a special value,
 * <code>@NULL@</code>, to indicate that you want to filter out the entity if
 * the field is null or empty.
 * <li><b>separator</b> Optional. The value separator. Defaults to the comma
 * character.
 * <li><b>ignoreCase</b> Optional. If "true", comparisons are made in a
 * case-insenstive way. Defaults to "false".
 * <li><b>invert</b> Optional. If "true", the filter is inverted. In other
 * words, the entity will <b>only</b> be written to the data set if the field
 * has one of the values in the list. Defaults to "false".
 * </ol>
 * 
 * @author Ayco Holleman
 *
 */
public class SimpleEntityFilter implements IEntityFilter {

	private static final String NULL_STRING = "@NULL@";

	private Path path;
	private String separator = ",";
	private String[] values;
	private boolean ignoreCase;
	private boolean invert;

	@Override
	public void initialize(Map<String, String> args) throws EntityFilterInitializationException
	{
		String arg = args.get("path");
		if (arg == null || arg.isEmpty()) {
			String msg = "Missing or empty element <arg name=\"path\"> for "
					+ "entity filter \"GenericEntityFilter\"";
			throw new EntityFilterInitializationException(msg);
		}
		path = new Path(arg);
		arg = args.get("separator");
		if (arg != null && arg.length() != 0) {
			separator = arg;
		}
		invert = ConfigObject.isTrueValue(args.get("invert"));
		ignoreCase = ConfigObject.isTrueValue(args.get("ignoreCase"));
		arg = args.get("values");
		if (arg == null || arg.isEmpty()) {
			String msg = "Missing or empty element <arg name=\"values\"> for "
					+ "entity filter \"GenericEntityFilter\"";
			throw new EntityFilterInitializationException(msg);
		}
		values = arg.split(separator);
		if (ignoreCase) {
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].toUpperCase();
			}
		}
	}

	@Override
	public boolean accept(EntityObject entity) throws EntityFilterException
	{
		Object value = path.read(entity.getData());
		String s;
		if (value == JsonUtil.MISSING_VALUE) {
			s = NULL_STRING;
		}
		else if (ignoreCase) {
			s = value.toString().toUpperCase();
		}
		else {
			s = value.toString();
		}
		for (String v : values) {
			if (s.equals(v)) {
				return invert;
			}
		}
		return !invert;
	}

}
