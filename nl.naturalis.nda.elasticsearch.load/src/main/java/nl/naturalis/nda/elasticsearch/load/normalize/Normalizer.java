package nl.naturalis.nda.elasticsearch.load.normalize;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;

import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;

/**
 * A Normalizer maps found-in-the-wild values to canonical equivalents. It
 * standardizes the set of allowed values for a particular field in a domain
 * model class (e.g. the sex field in the Specimen class). Normalizers enable
 * stronger typing in the domain model classes by using {@link Enum enum} fields
 * rather than free-text fields. Each normalizer serves to map a
 * found-in-the-wild value to a constant in an {@code enum} class.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The {@link Enum enum} class for which this normalizer was created.
 *            This {@code enum} class is also the maintainer of the canonical values.
 *            That is, calling {@code toString()} on any of the enum's constants
 *            yields a canonical value.
 */
public class Normalizer<T extends Enum<T>> {

	private boolean skipHeader = false;
	private String delimiter = ";";

	private final HashMap<String, String> mappings = new HashMap<>();
	private final T[] enumConstants;

	/**
	 * Creates a normalizer for the specified {@link Enum enumeration}.
	 * 
	 * @param enumClass
	 */
	public Normalizer(Class<T> enumClass)
	{
		enumConstants = enumClass.getEnumConstants();
	}

	/**
	 * Load/parse/cache the file that maps found-in-the-wild values to canonical
	 * values. Mapping files may include a special mapping:<br><br>
	 * {@code [NULL] : <canonical-value>}<br><br>
	 * For example:<br><br>
	 * {@code [NULL] : female}<br><br>
	 * This will cause empty fields in the data source to map to {@code female}.
	 * 
	 * @param translationFile
	 */
	public void loadMappings(File translationFile)
	{
		try (LineNumberReader lnr = new LineNumberReader(new FileReader(translationFile))) {
			if (skipHeader) {
				lnr.readLine();
			}
			String line;
			while ((line = lnr.readLine()) != null) {
				String[] parts = line.split(delimiter);
				if (parts.length != 2) {
					String fmt = "Invalid mapping in file %s: \"%s\"";
					String msg = String.format(fmt, translationFile.getAbsolutePath(), line);
					throw new ETLRuntimeException(msg);
				}
				mappings.put(parts[0].toLowerCase(), parts[1]);
			}
		}
		catch (IOException e) {
			throw new ETLRuntimeException(e);
		}
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
				mappings.put(parts[0].toLowerCase(), parts[1]);
			}
		}
		catch (IOException e) {
			throw new ETLRuntimeException(e);
		}
	}

	/**
	 * Returns the canonical value for the specified found-in-the-wild value.
	 * 
	 * @param input
	 * @return
	 */
	public String normalize(String input)
	{
		// You can have null mapped to a particular enum
		// constant if you like by mapping "[NULL]" to the
		// constant's toString() value
		if (input == null)
			return mappings.get("[NULL]");
		return mappings.get(input.toLowerCase());
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
		if (val != null) {
			for (T constant : enumConstants) {
				if (constant.toString().equals(val))
					return constant;
			}
		}
		return null;
	}

	/**
	 * Whether to skip the first line in the mappings file.
	 * 
	 * @return
	 */
	public boolean isSkipHeader()
	{
		return skipHeader;
	}

	/**
	 * Skips the first line in the mappings file.
	 * 
	 * @param skipHeader
	 */
	public void setSkipHeader(boolean skipHeader)
	{
		this.skipHeader = skipHeader;
	}

	/**
	 * Returns the string that separates the found-in-the-wild value from the
	 * canonical value.
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

}
