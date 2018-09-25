package nl.naturalis.nba.etl.normalize;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLRegistry;

import org.apache.logging.log4j.Logger;

/**
 * <p>
 * A Normalizer maps found-in-the-wild values to canonical values. It
 * standardizes the set of allowed values for a particular field in a domain
 * model class (e.g. the {@link PhaseOrStage phaseOrStage} or {@link Sex sex}
 * field in the {@link Specimen} class). Normalizers enable stronger typing in
 * the domain model classes by using {@link Enum enum} fields rather than
 * free-text fields.
 * </p>
 * <h3>Null and whitespace handling</h3>
 * <p>
 * You can map {@code null} values and whitespace to a canonical value,
 * effectively making that particular canonical value the default value. To do
 * so, include a mapping of the string "[NULL]" to that canonical value. For
 * example:<br>
 * 
 * <pre>
 * [NULL]:female
 * </pre>
 * 
 * Alternatively, you are also allowed to include the following mapping:<br>
 * 
 * <pre>
 * :female
 * </pre>
 * 
 * Conversely, you can also map certain rogue values to {@code null},
 * effectively acknowledging that they exist but choosing to ignore them. The
 * value "Unknown" might be an example of a rogue value that you want to map to
 * {@code null}. To do so, include the rogue value in your mapping file and map
 * it to the string "[NULL]". For example:<br>
 * 
 * <pre>
 * Unknown:[NULL]
 * </pre>
 * 
 * Alternatively, you are also allowed to include the following mapping:<br>
 * 
 * <pre>
 * Unknown:
 * </pre>
 * 
 * Note that if you would not include a mapping for "Unknown", the effect would
 * be the same: the field in question would be set to {@code null} for "Unknown"
 * values. However, if you include the mapping, your log file won't get
 * contaminated with unnecessary warnings telling you that a rogue value of
 * "Unknown" was found and ignored.
 * </p>
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The {@link Enum enum} class for which this normalizer was created.
 *            This {@code enum} class is also the maintainer of the canonical
 *            values. That is, calling {@code toString()} on <i>any</i> of the
 *            enum's constants yields a canonical value.
 */
public class Normalizer<T extends Enum<T>> {

	/**
	 * The value returned by {@link #normalize(String)} if no mapping was found
	 * for the argument passed to it. Compare the return value of
	 * {@code normalize} with {@code NOT_MAPPED} using the {@code ==} operator.
	 * Do not use the {@code equals()} method.
	 */
	public static final String NOT_MAPPED = new String();
	/**
	 * "[NULL]". String symbolizing {@code null} values and whitespace. You can
	 * include this string both as a key and as a value in your mappings.
	 */
	public static final String NULL_STRING = "[NULL]";

	private final Logger logger;
	private final HashMap<String, T> mappings;
	private final Class<T> enumClass;

	private boolean skipHeader = false;
	private boolean autoMapNull = true;
	private String delimiter = ";";

	private static class IntHolder {

		int i = 1;

		public String toString()
		{
			return String.valueOf(i);
		}
	}

	private HashMap<String, IntHolder> badValues;

	/**
	 * Creates a normalizer for the specified {@link Enum enumeration}.
	 * 
	 * @param enumClass
	 */
	public Normalizer(Class<T> enumClass)
	{
		logger = ETLRegistry.getInstance().getLogger(getClass());
		logger.info("Creating normalizer for " + enumClass.getSimpleName());
		this.enumClass = enumClass;
		mappings = new HashMap<>();
		badValues = new HashMap<>();
	}

	/**
	 * Parse/cache a string containing mappings for found-in-the-wild values to
	 * canonical values.
	 * 
	 * @param translations
	 * 
	 * @see #loadMappings(File)
	 */
	public void loadMappings(String translations)
	{
		logger.info("Caching canonicalizations for {}", enumClass.getSimpleName());
		try {
			LineNumberReader lnr = new LineNumberReader(new StringReader(translations));
			if (skipHeader) {
				lnr.readLine();
			}
			String line;
			while ((line = lnr.readLine()) != null) {
				String[] parts = line.split(delimiter);
				if (parts.length != 2) {
					throw new ETLRuntimeException(String.format("Invalid mapping: \"%s\"", line));
				}
				String key = parts[0].trim();
				String val = parts[1].trim();
				if (key.equals(NULL_STRING) || key.length() == 0)
					key = null;
				else
					key = key.toLowerCase();
				T constant = null;
				if (!val.equals(NULL_STRING) && val.length() != 0) {
					constant = find(val);
					/*
					 * Make sure canonical values always and only map to
					 * themselves. In other words each value in the HashMap must
					 * also be a key that maps to itself.
					 */
					if (mappings.containsKey(constant.toString())) {
						T self = mappings.get(constant.toString());
						if (constant != self) {
							String fmt = "\"%s\" is a canonical value and can therefore "
									+ "not be mapped to another value (\"%s\")";
							String msg = String.format(fmt, constant, self);
							throw new ETLRuntimeException(msg);
						}
					}
					else {
						mappings.put(constant.toString(), constant);
					}
				}
				mappings.put(key, constant);
			}
			if (autoMapNull && !mappings.containsKey(null)) {
				mappings.put(null, null);
			}
			logger.info("Number of mapped values: " + mappings.size());
			logger.info("Number of canonical values: " + new HashSet<>(mappings.values()).size());
		}
		catch (IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	/**
	 * Returns the canonical value for the specified found-in-the-wild value. If
	 * no mappings exists for the specified value, a special value is returned:
	 * {@link #NOT_MAPPED}. This allows clients to distinguish between input
	 * that could not be mapped to a canonical value (illegal input) and input
	 * that was explicitly mapped to {@code null} (valid input). Clients should
	 * compare references rather than use the {@code equals} method when
	 * comparing the result of {@link #normalize(String)} with
	 * {@link #NOT_MAPPED}.
	 * 
	 * @param input
	 * @return
	 */
	public String normalize(String input)
	{
		if (input != null)
			input = input.toLowerCase();
		if (!mappings.containsKey(input)) {
			IntHolder ih = badValues.get(input);
			if (ih == null)
				badValues.put(input, new IntHolder());
			else
				ih.i++;
			return NOT_MAPPED;
		}
		T t = mappings.get(input);
		return t == null ? null : t.toString();
	}

	/**
	 * Maps the specified found-in-the-wild value to an enum constant of type T.
	 * 
	 * @param input
	 * @return
	 */
	public T map(String input) throws UnmappedValueException
	{
		if (input != null) {
			input = input.toLowerCase();
		}
		if (mappings.containsKey(input)) {
			return mappings.get(input);
		}
		IntHolder ih = badValues.get(input);
		if (ih == null) {
			badValues.put(input, new IntHolder());
		}
		else {
			ih.i++;
		}
		throw new UnmappedValueException(input, enumClass);
	}

	 /**
   * Maps the specified found-in-the-wild value to an enum constant of type T.
   * 
   * @param input
   * @return
   */
  public String mapToString(String input) throws UnmappedValueException
  {
    if (input != null) {
      input = input.toLowerCase();
    }
    if (mappings.containsKey(input)) {
      return mappings.get(input).toString();
    }
    IntHolder ih = badValues.get(input);
    if (ih == null) {
      badValues.put(input, new IntHolder());
    }
    else {
      ih.i++;
    }
    throw new UnmappedValueException(input, enumClass);
  }
	
	/**
	 * Resets the rogue value counters.
	 */
	public void resetStatistics()
	{
		badValues = new HashMap<>();
	}

	/**
	 * For each rogue value print out how often it was encountered.
	 */
	public void logStatistics()
	{
		for (Map.Entry<String, IntHolder> entry : badValues.entrySet()) {
			String fmt = "Invalid value \"%s\" occurs in at least %s records";
			String msg = String.format(fmt, entry.getKey(), entry.getValue());
			logger.info(msg);
		}
	}

	/**
	 * Whether to skip the first line in the mappings file. Default
	 * {@code false}.
	 * 
	 * @return
	 */
	public boolean isSkipHeader()
	{
		return skipHeader;
	}

	/**
	 * Determines whether to skip the first line in the mappings file.
	 * 
	 * @param skipHeader
	 */
	public void setSkipHeader(boolean skipHeader)
	{
		this.skipHeader = skipHeader;
	}

	/**
	 * Returns the string that separates the found-in-the-wild value from the
	 * canonical value. Default ';' (semi-colon).
	 * 
	 * @return
	 */
	public String getDelimiter()
	{
		return delimiter;
	}

	/**
	 * Sets the string that separates the found-in-the-wild value from the
	 * canonical value.
	 * 
	 * @param delimiter
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

	/**
	 * Whether or not to automatically map whitespace to {@code null} if no
	 * explicit mapping is provided (see {@link Normalizer class description}.
	 * Default {@code true}. If {@code false}, whitespace will be treated as an
	 * illegal value and {@link #normalize(String)} will return
	 * {@link #NOT_MAPPED} in stead of simply {@code null}.
	 * 
	 * @return
	 */
	public boolean isAutoMapNull()
	{
		return autoMapNull;
	}

	/**
	 * Determines whether or not to automatically map {@code null} values to
	 * {@code null} if no explicit mapping was provided.
	 * 
	 * @param autoMapNull
	 */
	public void setAutoMapNull(boolean autoMapNull)
	{
		this.autoMapNull = autoMapNull;
	}

	private T find(String val)
	{
		for (T constant : enumClass.getEnumConstants()) {
			if (constant.toString().equals(val)) {
				return constant;
			}
		}
		String fmt = "No constant of type %s has been declared for canonical value \"%s\"";
		String msg = String.format(fmt, enumClass.getSimpleName(), val);
		throw new ETLRuntimeException(msg);
	}

}
