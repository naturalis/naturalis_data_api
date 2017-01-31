package nl.naturalis.nba.common;

import static java.lang.System.arraycopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
		if (obj.getClass() == Path.class)
			return Arrays.deepEquals(this.elems, ((Path) obj).elems);
		return false;
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

	private static String[] split(String path)
	{
		return path.split("\\.");
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

}
