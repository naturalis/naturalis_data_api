package nl.naturalis.nba.common;

import static java.lang.System.identityHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import nl.naturalis.nba.api.Path;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class PathValueComparator<T> implements Comparator<T> {

	private Path path;
	private boolean last;
	private HashMap<Integer, List> cache;

	public PathValueComparator(Path path)
	{
		this(path, false, 16);
	}

	public PathValueComparator(Path path, boolean last)
	{
		this(path, last, 16);
	}

	public PathValueComparator(Path path, int listSize)
	{
		this(path, false, listSize);
	}

	public PathValueComparator(Path path, boolean last, int listSize)
	{
		this.path = path;
		this.last = last;
		this.cache = new HashMap<>(((int) (listSize / .75F)) + 1);
	}

	@Override
	public int compare(T o1, T o2)
	{
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
				cache.put(identityHashCode(o1), values1);
			}
		}
		catch (InvalidPathException e) {
			throw new RuntimeException(e);
		}
		if (values1.isEmpty()) {
			if (values2.isEmpty()) {
				return 0;
			}
			return Integer.MAX_VALUE;
		}
		if (values2.isEmpty()) {
			return Integer.MIN_VALUE;
		}
		return 0;
	}

}
