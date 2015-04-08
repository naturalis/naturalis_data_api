package nl.naturalis.nda.elasticsearch.load.normalize;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;

public class Normalizer<T extends Enum<T>> {

	private boolean skipHeader = false;
	private String delimiter = ";";

	private final HashMap<String, String> mappings = new HashMap<String, String>();
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
					throw new RuntimeException(String.format("Invalid mapping: \"%s\"", line));
				}
				mappings.put(parts[0].toLowerCase(), parts[1]);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public String getNormalizedValue(String input)
	{
		// You can have null mapped to a particular enum
		// constant if you like by mapping "[NULL]" to the
		// constant's toString() value
		if (input == null) {
			return mappings.get("[NULL]");
		}
		return mappings.get(input.toLowerCase());

	}


	public T getEnumConstant(String input)
	{
		String normalizedValue = getNormalizedValue(input);
		if (normalizedValue != null) {
			for (T constant : enumConstants) {
				if (constant.toString().equals(normalizedValue)) {
					return constant;
				}
			}
		}
		return null;
	}


	public boolean isSkipHeader()
	{
		return skipHeader;
	}


	public void setSkipHeader(boolean skipHeader)
	{
		this.skipHeader = skipHeader;
	}


	public String getDelimiter()
	{
		return delimiter;
	}


	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}

}
