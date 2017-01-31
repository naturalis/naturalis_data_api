package nl.naturalis.nba.common;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;

public class PathUtil {

	private PathUtil()
	{
	}

	/**
	 * Whether or not the path denotes an array object within the document
	 * represented by the specified type mapping.
	 * 
	 * @param mapping
	 * @return
	 * @throws InvalidPathException
	 */
	public static boolean isArray(Path path, Mapping<?> mapping) throws InvalidPathException
	{
		MappingInfo<?> mappingInfo = new MappingInfo<>(mapping);
		ESField esField;
		try {
			esField = mappingInfo.getField(path.getPurePath());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidPathException(e.getMessage());
		}
		return esField.isArray();
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

	/*
	 * Make sure path elements representing a primitive value are NOT followed
	 * by array index.
	 */
	private static void checkIllegalArrayIndices(Path path, MappingInfo<?> mi)
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

	/*
	 * Make sure path elements representing an array are followed by an array
	 * index.
	 */
	private static void checkRequiredArrayIndices(Path path, MappingInfo<?> mi)
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
