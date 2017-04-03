package nl.naturalis.nba.api;

import static java.lang.System.arraycopy;

import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Immutable class representing a path within an Elasticsearch document. A path
 * is represented as a dot-separated string of path elements. For example:
 * {@code gatheringEvent.dateTimeBegin}. The {@code Path} class supports array
 * access through the following notation:
 * {@code identications.0.defaultClassification.kingdom}. However a path in
 * {@link QueryCondition query condition} must always be a "pure" path without
 * array indices.
 * 
 * @author Ayco Holleman
 *
 */
public final class Path {

	private String[] elems;

	// Caches value for toString()
	private String str;

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
	@JsonCreator
	public Path(String path)
	{
		this.elems = split(str = path);
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
	 * Returns the path element at the specified index as a new {@code Path}.
	 * 
	 * @param index
	 * @return
	 */
	public Path element(int index)
	{
		return new Path(elems[index]);
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
	 * Returns a new {@code Path} with the path element at the specified index
	 * replaced by the new element value.
	 * 
	 * @param element
	 * @param newValue
	 * @return
	 */
	public Path replace(int element, String newValue)
	{
		String[] copy = Arrays.copyOf(elems, elems.length);
		copy[element] = newValue;
		return new Path(copy);
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

	/**
	 * Returns this {@code Path} as a string.
	 * 
	 * @return
	 */
	@Override
	@JsonValue
	public String toString()
	{
		if (str == null) {
			StringBuilder sb = new StringBuilder(elems.length << 4);
			for (String e : elems) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(e);
			}
			return (str = sb.toString());
		}
		return str;
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
