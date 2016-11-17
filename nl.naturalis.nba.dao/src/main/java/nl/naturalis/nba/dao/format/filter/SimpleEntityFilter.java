package nl.naturalis.nba.dao.format.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.EntityFilterException;
import nl.naturalis.nba.dao.format.EntityFilterInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IEntityFilter;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * A generic entity filter that will suit for many use cases. The {@link Map}
 * passed to the {@link #initialize(Map) initialize} method may contain the
 * following arguments:
 * <ol>
 * <li><b>path</b> Required. Specifies the path of the field containing the
 * values to filter on.
 * <li><b>values</b> Required. A comma-separated list of values to filter on.
 * The entity will <b>only</b> be written to the data set if the specified field
 * has one of the values in the list. You may provide a special value,
 * <code>@NULL@</code>, to indicate that you want include entities if their
 * value for the specified field is null or empty.
 * <li><b>invert</b> Optional. If {@code true}, the filter is inverted. In other
 * words, the entity will <b>not</b> be written to the data set if the field has
 * any of the values in the list. Defaults to {@code true}.
 * <li><b>separator</b> Optional. The value separator. Defaults to the comma
 * character.
 * <li><b>ignoreCase</b> Optional. If {@code true}, comparisons are made in a
 * case-insenstive way. Defaults to {@code false}.
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

	// Whether or not one of the words in the <values> element was @NULL@
	private boolean containsNullString;

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
		String[] words = arg.split(separator);
		List<String> wordList = new ArrayList<>(words.length);
		for (String word : words) {
			if (word.equals(NULL_STRING)) {
				containsNullString = true;
			}
			else {
				wordList.add(word);
			}
		}
		values = wordList.toArray(new String[wordList.size()]);
	}

	@Override
	public boolean accept(EntityObject entity) throws EntityFilterException
	{
		boolean accept = !invert;
		Object value = path.read(entity.getData());
		if (value == JsonUtil.MISSING_VALUE) {
			return containsNullString ? accept : !accept;
		}
		if (ignoreCase) {
			for (String s : values) {
				if (s.equalsIgnoreCase(value.toString())) {
					return accept;
				}
			}
			return !accept;
		}
		for (String s : values) {
			if (s.equals(value.toString())) {
				return accept;
			}
		}
		return !accept;
	}

}
