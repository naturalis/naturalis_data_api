package nl.naturalis.nba.dao.es.format;

import java.util.ArrayList;

import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInfo;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;

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

	private String path;
	private String[] elements;

	public Path(String path)
	{
		this.path = path;
	}

	public Path(String[] pathElements)
	{
		this.elements = pathElements;
	}

	public String getPath()
	{
		if (path == null) {
			StringBuilder sb = new StringBuilder(elements.length * 10);
			for (String e : getPathElements()) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(e);
			}
			path = sb.toString();
		}
		return path;
	}

	public String getPurePath()
	{
		String[] elems = getPathElements();
		StringBuilder sb = new StringBuilder(elements.length * 10);
		for (String e : elems) {
			try {
				Integer.parseInt(e);
			}
			catch (NumberFormatException exc) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(e);
			}
		}
		return sb.toString();
	}

	public String[] getPathElements()
	{
		if (elements == null)
			elements = path.split("\\.");
		return elements;
	}

	public String[] getPurePathElements()
	{
		String[] elems = getPathElements();
		ArrayList<String> list = new ArrayList<>(elems.length);
		for (String e : elems) {
			try {
				Integer.parseInt(e);
			}
			catch (NumberFormatException exc) {
				list.add(e);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public void validate(DocumentType<?> documentType) throws EntityConfigurationException
	{
		MappingInfo mi = new MappingInfo(documentType.getMapping());
		checkArrayIndices(mi);
		try {
			ESField esField = mi.getField(getPurePath());
			if (!(esField instanceof DocumentField)) {
				String fmt = "Invalid field (type is object/nested): %s";
				String msg = String.format(fmt, path);
				throw new EntityConfigurationException(msg);
			}
		}
		catch (NoSuchFieldException e) {
			throw new EntityConfigurationException(e.getMessage());
		}
	}

	/*
	 * Make sure array indices are used only if the preceding path element
	 * refers to a nested array. TODO: we should actually also check that array
	 * indices are NOT used if the preceding path element is NOT an array.
	 */
	private void checkArrayIndices(MappingInfo mi) throws EntityConfigurationException
	{
		StringBuilder sb = new StringBuilder(50);
		for (String element : getPathElements()) {
			try {
				int index = Integer.parseInt(element);
				ESField esField = mi.getField(sb.toString());
				if (!esField.isMultiValued()) {
					String fmt = "Illegal array index (%s) following single-valued field: %s";
					String msg = String.format(fmt, index, sb.toString());
					throw new EntityConfigurationException(msg);
				}
			}
			catch (NumberFormatException e) {
				if (sb.length() != 0)
					sb.append('.');
				sb.append(element);
			}
		}
	}

}
