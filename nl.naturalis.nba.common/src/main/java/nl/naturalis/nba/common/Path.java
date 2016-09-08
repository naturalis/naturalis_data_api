package nl.naturalis.nba.common;

import static java.lang.System.arraycopy;

import java.util.ArrayList;
import java.util.Arrays;

import nl.naturalis.nba.common.es.map.DocumentField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

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
public final class Path {

	private final String[] elems;

	public Path(String path)
	{
		this.elems = split(path);
	}

	public Path(String[] elements)
	{
		this.elems = elements;
	}

	public Path(Path other)
	{
		this.elems = new String[other.elems.length];
		arraycopy(other.elems, 0, this.elems, 0, this.elems.length);
	}

	public String getPathString()
	{
		StringBuilder sb = new StringBuilder(elems.length << 4);
		for (String e : elems) {
			if (sb.length() != 0)
				sb.append('.');
			sb.append(e);
		}
		return sb.toString();
	}

	public String getElement(int index)
	{
		return elems[index];
	}

	public int countElements()
	{
		return elems.length;
	}

	public Path element(int index)
	{
		return new Path(new String[] { elems[index] });
	}

	public Path getPurePath()
	{
		ArrayList<String> list = new ArrayList<>(elems.length);
		for (String e : elems) {
			if (!isInteger(e)) {
				list.add(e);
			}
		}
		return new Path(list.toArray(new String[list.size()]));
	}

	public Path append(Path other)
	{
		int size = elems.length + other.elems.length;
		String[] concatenated = new String[size];
		arraycopy(elems, 0, concatenated, 0, elems.length);
		arraycopy(other.elems, 0, concatenated, elems.length, other.elems.length);
		return new Path(concatenated);
	}

	public Path shift()
	{
		String[] shifted = new String[elems.length - 1];
		arraycopy(elems, 1, shifted, 0, elems.length - 1);
		return new Path(shifted);
	}

	public void validate(Mapping mapping) throws InvalidPathException
	{
		MappingInfo mappingInfo = new MappingInfo(mapping);
		checkPathComplete(mappingInfo);
		checkRequiredArrayIndices(mappingInfo);
		checkIllegalArrayIndices(mappingInfo);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj.getClass() != Path.class)
			return false;
		return Arrays.deepEquals(this.elems, ((Path) obj).elems);
	}

	@Override
	public int hashCode()
	{
		return Arrays.deepHashCode(elems);
	}

	@Override
	public String toString()
	{
		return getPathString();
	}

	/*
	 * Make sure path references a simple, primitive value (not an object).
	 */
	private void checkPathComplete(MappingInfo mi) throws InvalidPathException
	{
		try {
			ESField esField = mi.getField(getPurePath().getPathString());
			if (!(esField instanceof DocumentField)) {
				String msg = "Incomplete path: %s" + getPathString();
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
