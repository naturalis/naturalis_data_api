package nl.naturalis.nda.elasticsearch.load.normalize;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;

import nl.naturalis.nda.domain.PhaseOrStage;
import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.crs.CrsSpecimenImportOffline;

import org.slf4j.Logger;

/**
 * <p>
 * A Normalizer maps found-in-the-wild values to canonical values. It
 * standardizes the set of allowed values for a particular field in a domain
 * model class (e.g. the {@link PhaseOrStage phaseOrStage} field in the
 * {@link Specimen} class). Normalizers enable stronger typing in the domain
 * model classes by using {@link Enum enum} fields rather than free-text fields.
 * </p>
 * <h3>Null and whitespace handling</h3>
 * <p>
 * You can map {@code null} values and whitespace to a particular canonical
 * value, effectively making that canonical value the default value. To do so,
 * include a mapping of the string "[NULL]" to that canonical value. For
 * example:<br>
 * 
 * <pre>
 * [NULL]:accepted name
 * </pre>
 * 
 * Alternatively, you are also allowed to include the following mapping:<br>
 * 
 * <pre>
 * :accepted name
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
 * be the same: the field in question would be set to null {@code null} for
 * "Unknown" values. However, if you include the mapping, your log file won't
 * get contaminated with unnecessary warnings telling you that a rogue value of
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
	 * for the specified argument.
	 */
	public static final String ROGUE_VALUE = new String();

	private static final String NULL_STRING = "[NULL]";
	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(CrsSpecimenImportOffline.class);
	}

	private final HashMap<String, String> mappings = new HashMap<>();
	private final T[] enumConstants;

	private boolean skipHeader = false;
	private String delimiter = ";";
	private boolean autoMapNull = true;

	/**
	 * Creates a normalizer for the specified {@link Enum enumeration}.
	 * 
	 * @param enumClass
	 */
	public Normalizer(Class<T> enumClass)
	{
		logger.info("Creating normalizer for " + enumClass.getSimpleName());
		enumConstants = enumClass.getEnumConstants();
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
		String type = enumConstants[0].getClass().getSimpleName();
		String msg = String.format("Caching canonicalizations for %s", type);
		logger.info(msg);
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
				String key = parts[0];
				String val = parts[1];
				if (key.equals(NULL_STRING) || key.trim().length() == 0) {
					key = null;
				}
				else {
					key = key.toLowerCase();
				}
				if (val.equals(NULL_STRING) || val.trim().length() == 0) {
					val = null;
				}
				mappings.put(key, val);
			}
			if (autoMapNull && !mappings.containsKey(null))
				mappings.put(null, null);
			logger.info("Number of canonical values: " + enumConstants.length);
			logger.info("Number of mapped values: " + mappings.size());
		}
		catch (IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	/**
	 * Returns the canonical value for the specified found-in-the-wild value. If
	 * no mappings exists for the specified value, a special value is returned:
	 * {@link #ROGUE_VALUE}. This allows clients to distinguish between input
	 * that could not be mapped to a canonical value (illegal input) and input
	 * that was explicitly mapped to {@code null} (valid input). To be on the
	 * safe side, clients should compare references rather than use the
	 * {@code equals} method when comparing the result of
	 * {@link #normalize(String)} with {@link #ROGUE_VALUE}.
	 * 
	 * @param input
	 * @return
	 */
	public String normalize(String input)
	{
		if (input != null)
			input = input.toLowerCase();
		if (!mappings.containsKey(input))
			return ROGUE_VALUE;
		return mappings.get(input);
	}

	/**
	 * Maps the specified found-in-the-wild value directly to an enum constant
	 * of type T.
	 * 
	 * @param input
	 * @return
	 */
	public T getEnumConstant(String input)
	{
		String val = normalize(input);
		if (val != null && val != ROGUE_VALUE) {
			for (T constant : enumConstants) {
				if (constant.toString().equals(val))
					return constant;
			}
		}
		return null;
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
	 * Whether or not to automatically map {@code null} to {@code null} if no
	 * explicit mapping was provided. Default {@code true}. If {@code false},
	 * {@code null} values will be treated as rogue (illegal) values and
	 * {@link #normalize(String)} will return {@link #ROGUE_VALUE} in stead of
	 * simply {@code null}.
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

}
