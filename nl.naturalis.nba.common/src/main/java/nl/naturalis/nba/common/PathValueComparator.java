package nl.naturalis.nba.common;

import static java.lang.System.identityHashCode;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nba.api.Path;

/**
 * A {@link Comparator} that lets you specify which field of the objects being
 * compared to use in the comparison. The field is specified by means of a
 * {@link Path}. The class of the objects denoted by the path must implement the
 * {@link Comparable} interface. If the type of object specified by the path is
 * an array or a {@link Collection}, then the <i>elements</i> of the
 * array/collection that must implement the {@code Comparable} interface. In
 * that case, the array/collection is first sorted, and then either the first or
 * the last element of the array/collection is used in the comparison. For
 * example, suppose you want to compare two specimens using the path
 * {@code identifications.scientificName.fullScientificName}. Since the
 * {@code identifications} element of this path is a {@link List}, for each of
 * the two objects being compared, all {@code fullScientificName} values are
 * collected first. Then they are sorted and then (depending on how you
 * configured the {@code PathValueComparator}) either the alphabetically
 * smallest or greatest {@code fullScientificName} of each of the specimens is
 * compared. This is a null-safe comparator; one or both of the objects passed
 * to {@link #compare(Object, Object) compare} may be null. Null values are
 * pushed to the end of the array or {@link List} when sorting it using this
 * comparator.
 * 
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PathValueComparator<T> implements Comparator<T> {

	private Path path;
	private boolean inverted;
	private boolean last;

	private HashMap<Integer, List> cache;

	/**
	 * Creates a {@code PathValueComparator} the compares objects using the
	 * specified {@link Path}. If the path denotes an array or a
	 * {@link Collection}, the array/collection is first sorted in ascending
	 * order, and then the first element of the array is used for the
	 * comparison.
	 * 
	 * @param path
	 */
	public PathValueComparator(Path path)
	{
		this(path, false, false, 16);
	}

	/**
	 * Creates a {@code PathValueComparator} the compares objects using the
	 * specified {@link Path}. If the {@code inverted} argument is {@code true},
	 * lists or arrays sorted using this comparator will be sorted in
	 * <i>descending</i> order of the values of {@code path}. If {@code path}
	 * denotes an array or a {@link Collection}, then the array/collection is
	 * first sorted and then the <i>last</i> element of the array is used for
	 * the comparison. If the {@code inverted} argument is {@code false}, lists
	 * or arrays sorted using this comparator will be sorted in <i>ascending</i>
	 * order of the values of {@code path}. If {@code path} denotes an array or
	 * a {@link Collection}, then the array/collection is first sorted and then
	 * the <i>first</i> element of the array is used for the comparison.
	 * 
	 * @param path
	 * @param inverted
	 */
	public PathValueComparator(Path path, boolean inverted)
	{
		this(path, inverted, inverted, 16);
	}

	/**
	 * Creates a {@code PathValueComparator} the compares objects using the
	 * specified {@link Path}. If the {@code inverted} argument is {@code true},
	 * lists or arrays sorted using this comparator will be sorted in
	 * <i>descending</i> order of the values of {@code path}. If {@code path}
	 * denotes an array or a {@link Collection}, then the array/collection is
	 * first sorted and then either the first or the last element of the array
	 * is used for the comparison, depending on the value of the {@code last}
	 * argument.
	 * 
	 * @param path
	 * @param inverted
	 * @param last
	 */
	public PathValueComparator(Path path, boolean inverted, boolean last)
	{
		this.path = path;
		this.inverted = inverted;
		this.last = last;
		this.cache = new HashMap<>();
	}

	/**
	 * Creates a {@code PathValueComparator} the compares objects using the
	 * specified {@link Path}. If the {@code inverted} argument is {@code true},
	 * lists or arrays sorted using this comparator will be sorted in
	 * <i>descending</i> order of the values of {@code path}. If {@code path}
	 * denotes an array or a {@link Collection}, then the array/collection is
	 * first sorted and then either the first or the last element of the array
	 * is used for the comparison, depending on the value of the {@code last}
	 * argument. The {@code listSize} argument should specify the exact length
	 * of the array or list to be sorted using this comparator.
	 * 
	 * @param path
	 * @param inverted
	 * @param last
	 * @param listSize
	 */
	public PathValueComparator(Path path, boolean inverted, boolean last, int listSize)
	{
		this.path = path;
		this.inverted = inverted;
		this.last = last;
		this.cache = new HashMap<>(listSize + 1, 1F);
	}

	@Override
	public int compare(T o1, T o2)
	{
		System.out.println("xxx: " + Math.signum(0));
		/*
		 * Push null values to the end of lists or arrays sorted using this
		 * comparator
		 */
		if (o1 == null)
			return o2 == null ? 0 : Integer.MAX_VALUE;
		if (o2 == null)
			return Integer.MIN_VALUE;
		List values1 = cache.get(identityHashCode(o1));
		List values2 = cache.get(identityHashCode(o2));
		try {
			if (values1 == null) {
				values1 = new PathReader(path).readValue(o1);
				Collections.sort(values1);
				cache.put(identityHashCode(o1), values1);
			}
			if (values2 == null) {
				values2 = new PathReader(path).readValue(o2);
				Collections.sort(values2);
				cache.put(identityHashCode(o2), values2);
			}
		}
		catch (InvalidPathException e) {
			throw new IllegalArgumentException(e);
		}
		/*
		 * Push null values for the field that is compared to just before the
		 * end of the list
		 */
		if (values1.isEmpty())
			return values2.isEmpty() ? 0 : 1;
		if (values2.isEmpty())
			return -1;
		Comparable c1;
		Comparable c2;
		if (last) {
			c1 = (Comparable) values1.get(values1.size() - 1);
			c2 = (Comparable) values2.get(values2.size() - 1);
		}
		else {
			c1 = (Comparable) values1.get(0);
			c2 = (Comparable) values2.get(0);
		}
		return inverted ? sign(c2.compareTo(c1)) : sign(c1.compareTo(c2));
	}

	private static int sign(int i)
	{
		return i == 0 ? 0 : (i < 0 ? -1 : 1);
	}

}
