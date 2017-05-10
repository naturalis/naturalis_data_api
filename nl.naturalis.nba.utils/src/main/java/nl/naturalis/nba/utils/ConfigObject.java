package nl.naturalis.nba.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * A class decorating the {@link java.util.Properties} class, providing useful
 * extra functionality on top of it.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings({ "synthetic-access", "serial" })
public class ConfigObject {

	public static class ConfigObjectException extends RuntimeException {

		public ConfigObjectException(String message)
		{
			super(message);
		}
	}

	/**
	 * Exception thrown when a required property turns out to be missing
	 */
	public static class MissingPropertyException extends ConfigObjectException {

		private MissingPropertyException(String property)
		{
			super("Missing property: " + property);
		}
	}

	/**
	 * Exception when a property was expected to have a non-whitespace value,
	 * but its value did in fact only contain whitespace.
	 */
	public static class PropertyNotSetException extends ConfigObjectException {

		private PropertyNotSetException(String property)
		{
			super("Property not set: " + property);
		}
	}

	/**
	 * Exception thrown when a property's value is invalid, illegal, unexpected,
	 * etc.
	 */
	@SuppressWarnings("serial")
	public static class InvalidValueException extends RuntimeException {

		private InvalidValueException(String property, String value)
		{
			super(String.format("Invalid value specified for property %s: \"%s\"", property,
					value));
		}
	}

	private static final String[] TRUE_VALUES = new String[] { "true", "1", "yes", "on", "ok" };

	public static ConfigObject forResource(String resource)
	{
		InputStream is = ConfigObject.class.getResourceAsStream(resource);
		return new ConfigObject(is);
	}

	/**
	 * Equivalent to {@code isTrueValue(value, false)}. In other words
	 * {@code null} and empty {@code String}s are considered to be {@code false}
	 * .
	 * 
	 * @param value
	 *            The {@code String} to convert to a boolean
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 */
	public static boolean isTrueValue(String value)
	{
		if (value == null) {
			return false;
		}
		return ArrayUtil.has(value.trim(), TRUE_VALUES);
	}

	/**
	 * Determines whether the specified value is a true-ish string. Returns
	 * {@code dfault} if the string is {@code null} or empty. Returns
	 * {@code true} if the specified value is "true", "1", "yes" "on" or "ok",
	 * {@code false} otherwise. The string is trimmed first and compared
	 * case-insensitively to these values.
	 * 
	 * @param value
	 *            The {@code String} to convert to a boolean
	 * @param dfault
	 *            The value to return if {@code value} is null or empty
	 * 
	 * @return
	 */
	public static boolean isTrueValue(String value, boolean dfault)
	{
		if (value == null) {
			return dfault;
		}
		value = value.trim();
		if (value.length() == 0) {
			return dfault;
		}
		return ArrayUtil.has(value, TRUE_VALUES);
	}

	/**
	 * Determines whether or not the specified system property has a true-ish
	 * value. If the property does not exist, {@code false} is returned.
	 * 
	 * @param propName
	 *            The name of the system property
	 * 
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 */
	public static boolean isEnabled(String propName)
	{
		return isEnabled(System.getProperties(), propName, false);
	}

	/**
	 * Determines if the specified property in the specified {@code Properties}
	 * object has a true-ish value. If the property does not exist,
	 * {@code false} is returned.
	 * 
	 * @param config
	 *            The {@code Properties} object
	 * @param propName
	 *            The name of the property
	 * 
	 * @return
	 */
	public static boolean isEnabled(Properties config, String propName)
	{
		return isEnabled(config, propName, true);
	}

	/**
	 * Determines whether or not the specified system property has a true-ish
	 * value. If the property does not exist, {@code dfault} is returned.
	 * 
	 * @param propName
	 *            The name of the property
	 * @param dfault
	 *            The value to return if the property does not exist or is an
	 *            empty string
	 * 
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 */
	public static boolean isEnabled(String propName, boolean dfault)
	{
		return isEnabled(System.getProperties(), propName, dfault);
	}

	/**
	 * Determines if the specified property in the specified {@code Properties}
	 * object has a true-ish value. If the property does not exist,
	 * {@code dfault} is returned.
	 * 
	 * @param config
	 *            The {@code Properties} object
	 * @param propName
	 *            The name of the property
	 * @param dfault
	 *            The value to return if the property does not exist or is an
	 *            empty string
	 * 
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 */
	public static boolean isEnabled(Properties config, String propName, boolean dfault)
	{
		return isTrueValue(config.getProperty(propName), dfault);
	}

	private final Properties config;
	private final String section;

	/**
	 * Create a {@code ConfigObject} from the specified properties file.
	 * 
	 * @param propertiesFile
	 */
	public ConfigObject(File propertiesFile)
	{
		section = StringUtil.EMPTY;
		config = new Properties();
		try {
			config.load(new FileInputStream(propertiesFile));
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}

	/**
	 * Create a {@code ConfigObject} from the specified {@code InputStream}.
	 * 
	 * @param is
	 */
	public ConfigObject(InputStream is)
	{
		section = StringUtil.EMPTY;
		config = new Properties();
		try {
			config.load(is);
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}

	/**
	 * Create a {@code ConfigObject} from the specified file system path,
	 * supposedly pointing to a properties file.
	 * 
	 * @param path
	 */
	public ConfigObject(String path)
	{
		section = StringUtil.EMPTY;
		config = new Properties();
		try {
			config.load(new FileInputStream(path));
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}

	public ConfigObject(Properties props)
	{
		section = StringUtil.EMPTY;
		config = props;
	}

	private ConfigObject(String section, Properties props)
	{
		this.section = section;
		this.config = props;
	}

	/**
	 * Check whether or not there is property with the specified name.
	 * 
	 * @param property
	 *            The name of the property to verify
	 * @return Whether or not there is property with the specified name
	 */
	public boolean hasProperty(String property)
	{
		return config.containsKey(property);
	}

	/**
	 * Get all properties names (keys) in this {@code ConfigObject}.
	 * 
	 * @return
	 */
	public List<String> getPropertyNames()
	{
		Set<Object> set = config.keySet();
		List<String> result = new ArrayList<>(set.size());
		for (Object obj : set) {
			if (obj instanceof String) {
				result.add((String) obj);
			}
		}
		return result;
	}

	/**
	 * Returns the {@code java.util.Properties} instance wrapped by this
	 * {@code ConfigObject}.
	 * 
	 * @return
	 */
	public Properties getProperties()
	{
		return config;
	}

	/**
	 * Get the value of the specified property. Equivalent to
	 * {@code get(property, null, true)}. This method behaves differently than
	 * {@link java.util.Properties#getProperty(String) Properties.getProperty()}
	 * . The latter method returns {@code null} if the property does not exist
	 * and an empty string if it does, but is not set. This method, on the other
	 * hand, returns {@code null} in both cases. To get the same behaviour as
	 * {@code Properties.getProperty()} use {@link #get(String, boolean)}.
	 * 
	 * @param property
	 * @return
	 */
	public String get(String property)
	{
		return get(property, null, true);
	}

	public int getInt(String property)
	{
		String s = get(property, "0", true);
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			throw new InvalidValueException(property, s);
		}
	}

	/**
	 * Get the value of the specified property. Equivalent to
	 * {@code get(property, dfault, true)}.
	 * 
	 * @param property
	 * @param dfault
	 * @return
	 */
	public String get(String property, String dfault)
	{
		return get(property, dfault, true);
	}

	/**
	 * Get the value of the specified property. Equivalent to
	 * {@code get(property, null, emptyIsDefault)}.
	 * 
	 * 
	 * @param property
	 * @param emptyIsNull
	 * @return
	 * 
	 * @see #get(String, String, boolean)
	 */
	public String get(String property, boolean emptyIsNull)
	{
		return get(property, null, emptyIsNull);
	}

	/**
	 * Get the value of the specified property.
	 * 
	 * @param property
	 *            The property whose value to get
	 * @param dfault
	 *            The value to return if the property does not exist
	 * @param emptyIsDefault
	 *            Whether or not to return the default value {@code dfault} if
	 *            the property is present but its value is an empty string
	 * 
	 * @return The value of the property
	 */
	public String get(String property, String dfault, boolean emptyIsDefault)
	{
		String val = $(property);
		if (val != null) {
			return (val.trim().isEmpty() && emptyIsDefault) ? dfault : val;
		}
		return null;
	}

	/**
	 * Get the value of the specified boolean property.
	 * 
	 * @param property
	 *            The property to look up
	 * 
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 * @see #isEnabled(Properties, String)
	 */
	public boolean isTrue(String property)
	{
		return isEnabled(config, property);
	}

	/**
	 * Get the value of the specified boolean property.
	 * 
	 * @param property
	 *            The property to look up
	 * @param dfault
	 *            The value to return if the property does not exist or is an
	 *            empty string.
	 * 
	 * @return
	 * 
	 * @see #isTrueValue(String, boolean)
	 * @see #isEnabled(Properties, String, boolean)
	 */
	public boolean isTrue(String property, boolean dfault)
	{
		return isEnabled(config, property, dfault);
	}

	/**
	 * Get the value of the specified property. The property is assumed to be a
	 * required property (it must exist and have a non-whitespace value). If the
	 * property does not exist a {@link MissingPropertyException} is thrown. If
	 * its value contains only whitespace, a {@link PropertyNotSetException} is
	 * thrown.
	 * 
	 * @param property
	 *            The property to look up
	 * @return The value of the specified property
	 * @throws MissingPropertyException
	 *             If the specified property does not exist
	 * @throws PropertyNotSetException
	 *             If the value of the property contains only whitespace
	 */
	public String required(String property)
	{
		if (config.containsKey(property)) {
			String s = $(property);
			if (s.trim().isEmpty()) {
				throw new PropertyNotSetException(fullName(property));
			}
			return s;
		}
		throw new MissingPropertyException(fullName(property));
	}

	/**
	 * Get the value of the specified property, cast to the specified type.
	 * Allowed types are: all primitive types, all primitive wrappers, and
	 * {@code String}. The property is assumed to be a required property (it
	 * must exist and have a non-whitespace value). If the property does not
	 * exist a {@link MissingPropertyException} is thrown. If its value contains
	 * only whitespace, a {@link PropertyNotSetException} is thrown.
	 * 
	 * @param property
	 * @param castTo
	 *            The class to cast the property's value to.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T required(String property, Class<T> castTo)
	{
		String val = required(property).trim();
		if (val.isEmpty()) {
			throw new MissingPropertyException(property);
		}
		if (castTo == String.class) {
			return (T) val;
		}
		else if (castTo == int.class || castTo == Integer.class) {
			return (T) Integer.valueOf(val);
		}
		else if (castTo == long.class || castTo == Long.class) {
			return (T) Long.valueOf(val);
		}
		else if (castTo == float.class || castTo == Float.class) {
			return (T) Float.valueOf(val);
		}
		else if (castTo == double.class || castTo == Double.class) {
			return (T) Double.valueOf(val);
		}
		else if (castTo == boolean.class || castTo == Boolean.class) {
			return (T) (isTrue(property) ? Boolean.TRUE : Boolean.FALSE);
		}
		else if (castTo == short.class || castTo == Short.class) {
			return (T) Short.valueOf(val);
		}
		else if (castTo == byte.class || castTo == Byte.class) {
			return (T) Byte.valueOf(val);
		}
		else if (castTo == char.class || castTo == Character.class) {
			if (val.length() != 1) {
				throw new InvalidValueException(property, val);
			}
			return (T) Character.valueOf(val.charAt(0));
		}
		String fmt = "Property %s: cannot cast to %s";
		String msg = String.format(fmt, fullName(property), castTo.getName());
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Get a {@code File} instance corresponding to the specified property. If
	 * the property does not point to an existing regular file, an
	 * {@link InvalidValueException} is thrown. The property is assumed to be a
	 * required property (it must exist and have a non-whitespace value). If the
	 * property does not exist a {@link MissingPropertyException} is thrown. If
	 * its value contains only whitespace, a {@link PropertyNotSetException} is
	 * thrown.
	 * 
	 * @param property
	 *            The path of the file
	 * @return A {@code File} object corresponding to the path
	 */
	public File getFile(String property)
	{
		File f = new File(required(property));
		if (!f.isFile()) {
			throw new InvalidValueException(fullName(property), $(property));
		}
		return f;
	}

	/**
	 * Get a {@code File} instance corresponding to the specified property. If
	 * the property does not point to an existing directory, an
	 * {@link InvalidValueException} is thrown. The property is assumed to be a
	 * required property (it must exist and have a non-whitespace value). If the
	 * property does not exist a {@link MissingPropertyException} is thrown. If
	 * its value contains only whitespace, a {@link PropertyNotSetException} is
	 * thrown.
	 * 
	 * @param property
	 * @return
	 */
	public File getDirectory(String property)
	{
		File f = new File(required(property));
		if (!f.isDirectory()) {
			throw new InvalidValueException(fullName(property), $(property));
		}
		return f;
	}

	/**
	 * Get a {@code Path} instance corresponding to the specified property. If
	 * the property does not point to an existing file or directory, an
	 * {@link InvalidValueException} is thrown. The property is assumed to be a
	 * required property (it must exist and have a non-whitespace value). If the
	 * property does not exist a {@link MissingPropertyException} is thrown. If
	 * its value contains only whitespace, a {@link PropertyNotSetException} is
	 * thrown.
	 * 
	 * @param property
	 * @return
	 */
	public Path getPath(String property)
	{
		Path path = FileSystems.getDefault().getPath(required(property));
		if (!path.toFile().exists()) {
			throw new InvalidValueException(fullName(property), $(property));
		}
		return path;
	}

	public String[] getArray(String property)
	{
		return getArray(property, ',');
	}

	public String[] getArray(String property, char separator)
	{
		String raw = get(property);
		if (raw == null)
			return null;
		String[] values = raw.split(String.valueOf(separator));
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

	public String[] requiredArray(String property)
	{
		return requiredArray(property, ',');
	}

	public String[] requiredArray(String property, char separator)
	{
		String raw = required(property);
		String[] values = raw.split(String.valueOf(separator));
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].trim();
		}
		return values;
	}

	/**
	 * If this {@link ConfigObject} was retrieved via
	 * {@link #getSection(String)}, this method will return the property prefix
	 * used to extract this {@link ConfigObject} from the encompassing
	 * {@link ConfigObject}. Otherwise this method returns an empty string.
	 * 
	 * @return
	 */
	public String getParentSection()
	{
		return this.section;
	}

	/**
	 * Returns a new {@code ConfigObject} instance containing only those
	 * properties whose name start with the specified prefix followed by a dot.
	 * In the new {@code ConfigObject} instance, the prefix (plus the dot) is
	 * chopped off the property names. For example, if the original
	 * {@code ConfigObject} contains the properties
	 * <ul>
	 * <li>db.user
	 * <li>db.password
	 * <li>db.name
	 * <li>host.ip
	 * <li>host.port
	 * </ul>
	 * then {@code getSection("db")} will return a new {@code ConfigObject}
	 * instance with the properties
	 * <li>user
	 * <li>password
	 * <li>name
	 * </ul>
	 * Calling {@link #getParentSection()} on the new {@code ConfigObject} will
	 * return "db".
	 * 
	 * @param prefix
	 * @return
	 */
	public ConfigObject getSection(String prefix)
	{
		Set<String> props = config.stringPropertyNames();
		Properties child = new Properties();
		String s = prefix + '.';
		for (String prop : props) {
			if (prop.startsWith(s)) {
				String newProp = StringUtil.lchop(prop, s);
				child.setProperty(newProp, $(prop));
			}
		}
		if (child.isEmpty()) {
			return null;
		}
		if (section == StringUtil.EMPTY) {
			return new ConfigObject(prefix, child);
		}
		String newSection = section + '.' + prefix;
		return new ConfigObject(newSection, child);
	}

	/**
	 * Whether or not there is at least one property starting with the specified
	 * prefix followed by a dot.
	 * 
	 * @param prefix
	 * @return
	 */
	public boolean hasSection(String prefix)
	{
		prefix += '.';
		for (String prop : config.stringPropertyNames()) {
			if (prop.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the names of all subsections within this configuration. For
	 * example, if a {@code ConfigObject} contains the properties
	 * <ul>
	 * <li>db.user
	 * <li>db.password
	 * <li>db.name
	 * <li>host.ip
	 * <li>host.port
	 * <li>baseUrl
	 * </ul>
	 * then {@code getSectionNames()} will return <code>["db", "host"]</code>.
	 * Note that the baseUrl property will not be listed as a subsection. Only
	 * properties with a dot in their name are considered to be grouped into
	 * sections. If no subsections were found (i.e. there were no properties
	 * with a dot in their name), then {@code null} is returned rather than a
	 * zero-length array.
	 * 
	 * @return
	 */
	public String[] getSubsections()
	{
		LinkedHashSet<String> sections = new LinkedHashSet<>();
		for (String prop : config.stringPropertyNames()) {
			int i = prop.indexOf('.');
			if (i != 0) {
				sections.add(prop.substring(0, i));
			}
		}
		if (sections.size() == 0) {
			return null;
		}
		return sections.toArray(new String[sections.size()]);
	}

	/**
	 * Returns the subsection of the specified parent section. For example, if a
	 * {@code ConfigObject} contains the properties
	 * <ul>
	 * <li>system.db.user
	 * <li>system.db.password
	 * <li>system.db.name
	 * <li>system.host.ip
	 * <li>system.host.port
	 * <li>baseUrl
	 * </ul>
	 * then {@code getSectionNames("system")} will return
	 * <code>["db", "host"]</code>.
	 * 
	 * @param parentSection
	 * @return
	 */
	public String[] getSubsections(String parentSection)
	{
		ConfigObject section = getSection(parentSection);
		if (section == null) {
			throw new ConfigObjectException("No such section: " + parentSection);
		}
		return section.getSubsections();
	}

	/**
	 * Set the value of the specified property.
	 * 
	 * @param property
	 *            The property to set
	 * @param value
	 *            The value to set it to
	 */
	public void set(String property, String value)
	{
		config.setProperty(property, value);
	}

	private String $(String property)
	{
		return config.getProperty(property);
	}

	private String fullName(String property)
	{
		if (section == StringUtil.EMPTY) {
			return property;
		}
		return section + '.' + property;
	}

}
