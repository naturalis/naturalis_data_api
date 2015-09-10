package nl.naturalis.nda.elasticsearch.load.normalize;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;

import nl.naturalis.nda.elasticsearch.load.ETLRuntimeException;

/**
 * A Normalizer maps found-in-the-wild values to their canonical equivalents.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            An {@code enum} class that maintains the canonical values. That
 *            is, calling {@code toString()} on any of the enum's constants
 *            yields a canonical value.
 */
public class Normalizer<T extends Enum<T>> {

	private boolean skipHeader = false;
	private String delimiter = ";";

	private final HashMap<String, String> mappings = new HashMap<>();
	private final T[] enumConstants;

	public Normalizer(Class<T> enumClass)
	{
		enumConstants = enumClass.getEnumConstants();
	}

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
					throw new RuntimeException(String.format("Invalid mapping in file %s: \"%s\"", translationFile.getAbsolutePath(), line));
				}
				mappings.put(parts[0].toLowerCase(), parts[1]);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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
