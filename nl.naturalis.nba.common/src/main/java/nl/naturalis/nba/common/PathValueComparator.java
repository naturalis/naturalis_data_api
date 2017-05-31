package nl.naturalis.nba.common;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.naturalis.nba.api.Path;

/**
 * A {@link Comparator} that compares objects based on the values of one or more
 * fields, dynamically referenced through {@link Path} objects. The type of the
 * field referenced by the {@code Path} object must implement the
 * {@link Comparable} interface. If the type of the field is an array or a
 * {@link List}, then the <i>elements</i> of the array/list must implement the
 * {@code Comparable} interface. In this case the array/list is first sorted,
 * and then either the first or the last element of the array/list is used for
 * the comparison, depending on how you configure the comparator. For example,
 * suppose you want to compare two specimens using the path
 * {@code identifications.scientificName.fullScientificName}. Since the
 * {@code identifications} element of this path is a {@link List}, for each of
 * the two objects being compared, all {@code fullScientificName} values are
 * collected first. These are then sorted according to their "natural order"
 * (see {@link Comparabale}), and then either the alphabetically smallest or
 * greatest {@code fullScientificName} of each of the specimens is compared.
 * 
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of the objects compared by this {@code Comparator}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PathValueComparator<T> implements Comparator<T> {

	/**
	 * Defines which field to compare and how.
	 * 
	 * @author Ayco Holleman
	 *
	 */
	public static class PathValueComparee {

		private Path path;
		private boolean inverted;
		private boolean last;

		/**
		 * Creates a {@code Comparee} that will cause the comparator to sort the
		 * objects in ascending order of the value denoted by the specified
		 * path.
		 * 
		 * @param path
		 */
		public PathValueComparee(Path path)
		{
			this(path, false, false);
		}

		/**
		 * Creates a {@code Comparee} that will cause the comparator to sort the
		 * objects in the order specified by the {@code inverted} argument. When
		 * {@code true} the objects will be sorted in descending order.
		 * Otherwise they will be sorted in ascending order. If the path denotes
		 * an array or a {@link List}, the array/list will first be sorted. When
		 * sorting in ascending order, the first element of the array/list is
		 * used for the comparison; otherwise the last element of the array/list
		 * is used for the comparison.
		 * 
		 * @param path
		 * @param inverted
		 */
		public PathValueComparee(Path path, boolean inverted)
		{
			this(path, inverted, inverted);
		}

		/**
		 * Creates a {@code Comparee} that will cause the comparator to sort the
		 * objects in the order specified by the {@code inverted} argument. When
		 * {@code true} the objects will be sorted in descending order.
		 * Otherwise they will be sorted in ascending order. If the path denotes
		 * an array or a {@link List}, the array/list will first be sorted. Then
		 * eother the first or the last element of the array/list will be used
		 * for the comparison, depending on the value of the {@code last}
		 * argument.
		 * 
		 * @param path
		 * @param inverted
		 * @param last
		 */
		public PathValueComparee(Path path, boolean inverted, boolean last)
		{
			this.path = path;
			this.inverted = inverted;
			this.last = last;
		}

	}

	private PathValueComparee[] comparees;

	/**
	 * See {@link PathValueComparee#Comparee(Path)}.
	 * 
	 * @param path
	 */
	public PathValueComparator(Path path)
	{
		comparees = new PathValueComparee[] { new PathValueComparee(path) };
	}

	/**
	 * See {@link PathValueComparee#Comparee(Path, boolean)}.
	 * 
	 * @param path
	 * @param inverted
	 */
	public PathValueComparator(Path path, boolean inverted)
	{
		comparees = new PathValueComparee[] { new PathValueComparee(path, inverted) };
	}

	/**
	 * Creates a comparator that compares one or more of the fields of the
	 * objects passed to the {@link #compare(Object, Object) compare} method.
	 * 
	 * @param comparees
	 */
	public PathValueComparator(PathValueComparee[] comparees)
	{
		this.comparees = comparees;
	}

	@Override
	public int compare(T o1, T o2)
	{
		int i;
		for (PathValueComparee comparee : comparees) {
			if ((i = compare(o1, o2, comparee)) != 0) {
				return sign(i);
			}
		}
		return 0;
	}

	private static int compare(Object o1, Object o2, PathValueComparee comparee)
	{
		if (o1 == null)
			return o2 == null ? 0 : Integer.MAX_VALUE;
		if (o2 == null)
			return Integer.MIN_VALUE;
		List values1 = null;
		List values2 = null;
		try {
			values1 = new NullSkippingPathValueReader(comparee.path).readValue(o1);
			values2 = new NullSkippingPathValueReader(comparee.path).readValue(o2);
		}
		catch (InvalidPathException e) {
			throw new IllegalArgumentException(e);
		}
		if (values1.isEmpty())
			return values2.isEmpty() ? 0 : 1;
		if (values2.isEmpty())
			return -1;
		Collections.sort(values1);
		Collections.sort(values2);
		Comparable c1;
		Comparable c2;
		if (comparee.last) {
			c1 = (Comparable) values1.get(values1.size() - 1);
			c2 = (Comparable) values2.get(values2.size() - 1);
		}
		else {
			c1 = (Comparable) values1.get(0);
			c2 = (Comparable) values2.get(0);
		}
		if (comparee.inverted)
			return c2.compareTo(c1);
		return c1.compareTo(c2);
	}

	private static int sign(int i)
	{
		return i == 0 ? 0 : (i < 0 ? -1 : 1);
	}

}