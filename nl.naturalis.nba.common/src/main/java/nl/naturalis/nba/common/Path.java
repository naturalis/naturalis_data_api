package nl.naturalis.nba.common;

import java.util.ArrayList;

import nl.naturalis.nba.common.es.map.DocumentField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

import static java.lang.System.arraycopy;

/**
 * Represents a path to a <i>single primitive</i> value in recursively nested
 * structures like Elasticsearch documents, Java objects or
 * Map&lt;String,Map&gt; objects. The path is always assumed to extend all the
 * way down to a primitive type (e.g. a string, integer, boolean, etc.); it
 * should not end with a field representing an array or object. Array access is
 * supported by including array indices in the path. For example:<br>
 * <code>
 * identications.0.defaultClassification.kingdom
 * </code><br>
 * The following paths are invalid:<br>
 * <code>
 * identications.defaultClassification.kingdom
 * identications.0.defaultClassification
 * </code><br>
 * The first path is invalid because it is ambiguous. The
 * <code>identifications</code> field within a Specimen document is an array, so
 * the next path element <i>must</i> be an array index. The second path is
 * invalid because the <code>defaultClassification</code> field represents an
 * object rather than a primitive value.
 * 
 * @author Ayco Holleman
 *
 */
public class Path {

	private String[] elems;

	public Path(String path)
	{
		this.elems = split(path);
	}

	public Path(String[] elements)
	{
		this.elems = elements;
	}

	public String getPath()
	{
		StringBuilder sb = new StringBuilder(elems.length << 4);
		for (String e : elems) {
			if (sb.length() != 0)
				sb.append('.');
			sb.append(e);
		}
		return sb.toString();
	}

	public String getPurePath()
	{
		StringBuilder sb = new StringBuilder(elems.length << 4);
		for (String e : elems) {
			if (!isInteger(e)) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(e);
			}
		}
		return sb.toString();
	}

	public String[] getElements()
	{
		return elems;
	}

	public String[] getPureElements()
	{
		ArrayList<String> list = new ArrayList<>(elems.length);
		for (String e : elems) {
			if (!isInteger(e)) {
				list.add(e);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public void append(Path path)
	{
		int size = elems.length + path.elems.length;
		String[] concatenated = new String[size];
		arraycopy(elems, 0, concatenated, 0, elems.length);
		arraycopy(path.elems, 0, concatenated, elems.length, path.elems.length);
		elems = concatenated;
	}

	public void validate(Mapping mapping) throws InvalidPathException
	{
		MappingInfo mappingInfo = new MappingInfo(mapping);
		checkPathComplete(mappingInfo);
		checkRequiredArrayIndices(mappingInfo);
		checkIllegalArrayIndices(mappingInfo);
	}

	/*
	 * Make sure path references a simple, primitive value (not an object).
	 */
	private void checkPathComplete(MappingInfo mi) throws InvalidPathException
	{
		try {
			ESField esField = mi.getField(getPurePath());
			if (!(esField instanceof DocumentField)) {
				String msg = "Incomplete path: %s" + getPath();
				throw new InvalidPathException(msg);
			}
		}
		catch (NoSuchFieldException e) {
			throw new InvalidPathException(e.getMessage());
		}
	}

	/*
	 * Make sure array indices are ALWAYS used if the preceding path element
	 * references an array.
	 */
	private void checkRequiredArrayIndices(MappingInfo mi) throws InvalidPathException
	{
		StringBuilder sb = new StringBuilder(elems.length << 4);
		for (int i = 0; i < elems.length; i++) {
			String element = elems[i];
			if (isInteger(element))
				continue;
			if (i != 0)
				sb.append('.');
			sb.append(element);
			ESField esField;
			try {
				esField = mi.getField(sb.toString());
			}
			catch (NoSuchFieldException e) {
				throw new InvalidPathException(e.getMessage());
			}
			if (esField.isMultiValued()) {
				if (i == elems.length - 1 || !isInteger(elems[i + 1])) {
					String fmt = "Array index required after multi-valued field %s";
					String msg = String.format(fmt, sb.toString());
					throw new InvalidPathException(msg);
				}
			}
		}
	}

	/*
	 * Make sure array indices are ONLY used if the preceding path element
	 * references an array.
	 */
	private void checkIllegalArrayIndices(MappingInfo mi) throws InvalidPathException
	{
		StringBuilder sb = new StringBuilder(elems.length << 4);
		for (int i = 0; i < elems.length; i++) {
			String element = elems[i];
			if (isInteger(element))
				continue;
			if (i != 0)
				sb.append('.');
			sb.append(element);
			ESField esField;
			try {
				esField = mi.getField(sb.toString());
			}
			catch (NoSuchFieldException e) {
				throw new InvalidPathException(e.getMessage());
			}
			if (!esField.isMultiValued()) {
				if (i < elems.length - 1 && isInteger(elems[i + 1])) {
					String fmt = "Illegal array index following single-valued field: %s";
					String msg = String.format(fmt, sb.toString());
					throw new InvalidPathException(msg);
				}
			}
		}
	}

	private static boolean isInteger(String s)
	{
		try {
			Integer.parseInt(s);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}

	private static String[] split(String path)
	{
		return path.split("\\.");
	}

}
