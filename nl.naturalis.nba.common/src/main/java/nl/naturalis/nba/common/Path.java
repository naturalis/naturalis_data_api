package nl.naturalis.nba.common;

import static java.lang.System.arraycopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * Immutable class representing a path within an Elasticsearch document. A path
 * is represented as a dot-separated string of path elements. For example:
 * {@code gatheringEvent.dateTimeBegin}. Each consecutive element denotes an
 * object nested ever more deeply within the document, except for the last
 * element, which could also be a "primitive" value (strings, numbers, dates,
 * etc.). Array access is supported by including array indices in the path. For
 * example: {@code identications.0.defaultClassification.kingdom}.
 * 
 * @author Ayco Holleman
 *
 */
public final class Path {

	private final String[] elems;

	/**
	 * Creates a new empty {@code Path}.
	 */
	public Path()
	{
		this.elems = new String[0];
	}

	/**
	 * Creates a new {@code Path} from the specified path string.
	 * 
	 * @param path
	 */
	public Path(String path)
	{
		this.elems = split(path);
	}

	/**
	 * Creates a new {@code Path} from the specified path elements.
	 * 
	 * @param elements
	 */
	public Path(String[] elements)
	{
		this.elems = new String[elements.length];
		arraycopy(elements, 0, this.elems, 0, elements.length);
	}

	/**
	 * Copy constructor. Creates a new {@code Path} from the specified
	 * {@code Path}.
	 * 
	 * @param other
	 */
	public Path(Path other)
	{
		this(other.elems);
	}

	/**
	 * Returns this {@code Path} as a string with all path elements joined and
	 * separated using the dot character.
	 * 
	 * @return
	 */
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

	/**
	 * Returns the path element at the specified index.
	 * 
	 * @param index
	 * @return
	 */
	public String getElement(int index)
	{
		return elems[index];
	}

	/**
	 * Returns the number of path elements in this {@code Path}.
	 * 
	 * @return
	 */
	public int countElements()
	{
		return elems.length;
	}

	/**
	 * Returns the path element at the specified index as a new {@code Path}.
	 * 
	 * @param index
	 * @return
	 */
	public Path element(int index)
	{
		return new Path(new String[] { elems[index] });
	}

	/**
	 * Returns a new {@code Path} containing only the elements of this
	 * {@code Path} that are not array indices.
	 * 
	 * @return
	 */
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

	/**
	 * Returns a new {@code Path} consisting of the elements of this
	 * {@code Path} plus the elements of the specified {@code Path}. NB
	 * {@code Path} being an immutable class, both this {@code Path} and the
	 * specified {@code Path} remain unchanged.
	 * 
	 * @param path
	 * @return
	 */
	public Path append(String path)
	{
		return append(new Path(path));
	}

	/**
	 * Returns a new {@code Path} consisting of the elements of this
	 * {@code Path} plus the elements of the specified {@code Path}. NB
	 * {@code Path} being an immutable class, both this {@code Path} and the
	 * specified {@code Path} remain unchanged.
	 * 
	 * @param other
	 * @return
	 */
	public Path append(Path other)
	{
		int size = elems.length + other.elems.length;
		String[] concatenated = new String[size];
		arraycopy(elems, 0, concatenated, 0, elems.length);
		arraycopy(other.elems, 0, concatenated, elems.length, other.elems.length);
		return new Path(concatenated);
	}

	/**
	 * Returns a new {@code Path} with the elements of this {@code Path} minus
	 * its first element.
	 * 
	 * @return
	 */
	public Path shift()
	{
		String[] shifted = new String[elems.length - 1];
		arraycopy(elems, 1, shifted, 0, elems.length - 1);
		return new Path(shifted);
	}

	/**
	 * <p>
	 * Validates that this instance represents an <b>unambiguous</b> path to a
	 * <b>primitive</b> value within an Elasticsearch document. The following
	 * paths would fail this test (given the {@link Specimen} document type):
	 * </p>
	 * <code>
	 * identications.defaultClassification.kingdom<br>
	 * identications.0.defaultClassification
	 * </code>
	 * <p>
	 * The first path is invalid because it is ambiguous: the
	 * <code>identifications</code> field within a Specimen document is an array
	 * so the next path element <i>must</i> be an array index indicating which
	 * element of the array you want. The second path is invalid because the
	 * <code>defaultClassification</code> field is an object rather than a
	 * primitive value.
	 * </p>
	 * 
	 */
	public static void validate(Path path, Mapping<?> mapping) throws InvalidPathException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(mapping);
		if (!isPrimitive(path, mapping)) {
			String msg = String.format("Incomplete path: %s", path);
			throw new InvalidPathException(msg);
		}
		checkRequiredArrayIndices(path, mappingInfo);
		checkIllegalArrayIndices(path, mappingInfo);
	}

	/**
	 * Whether or not the path denotes a primitive value within the document
	 * represented by the specified type mapping.
	 */
	public static boolean isPrimitive(Path path, Mapping<?> mapping) throws InvalidPathException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(mapping);
		ESField esField;
		try {
			esField = mappingInfo.getField(path.getPurePath());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidPathException(e.getMessage());
		}
		return (esField instanceof SimpleField);
	}

	/**
	 * Whether or not the path denotes an array object within the document
	 * represented by the specified type mapping.
	 * 
	 * @param mapping
	 * @return
	 * @throws InvalidPathException
	 */
	public boolean isArray(Mapping<?> mapping) throws InvalidPathException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(mapping);
		ESField esField;
		try {
			esField = mappingInfo.getField(getPurePath());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidPathException(e.getMessage());
		}
		return esField.isArray();
	}

	/**
	 * Extracts the value of field represented by this {@code Path} from the
	 * specified map. Equivalent to {@link JsonUtil#readField(Map, String)
	 * JsonUtil.readField(data, this)}. Note that the {@code Map<String,Object>}
	 * instance you pass to this method is precisely what is handed to you when
	 * making the Elasticsearch API call {@code SearchHit.getSource}.
	 * 
	 * @param data
	 * @return
	 */
	public Object read(Map<String, Object> data)
	{
		return JsonUtil.readField(data, this);
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
	 * Make sure path elements representing an array are followed by an array
	 * index.
	 */
	public static void checkRequiredArrayIndices(Path path, MappingInfo<?> mi)
			throws InvalidPathException
	{
		int len = path.countElements();
		StringBuilder sb = new StringBuilder(len << 4);
		for (int i = 0; i < len; i++) {
			String element = path.getElement(i);
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
			if (esField.isArray()) {
				if (i == len - 1 || !isInteger(path.getElement(i + 1))) {
					String fmt = "Array index required after multi-valued field %s";
					String msg = String.format(fmt, sb.toString());
					throw new InvalidPathException(msg);
				}
			}
		}
	}

	/*
	 * Make sure path elements representing a primitive value are NOT followed
	 * by array index.
	 */
	public static void checkIllegalArrayIndices(Path path, MappingInfo<?> mi)
			throws InvalidPathException
	{
		int len = path.countElements();
		StringBuilder sb = new StringBuilder(len << 4);
		for (int i = 0; i < len; i++) {
			String element = path.getElement(i);
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
			if (!esField.isArray()) {
				if (i < len - 1 && isInteger(path.getElement(i + 1))) {
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
