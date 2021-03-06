package nl.naturalis.nba.common.es.map;

import static nl.naturalis.nba.common.es.map.ESDataType.NESTED;
import static nl.naturalis.nba.common.es.map.ESDataType.OBJECT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.utils.CollectionUtil;
import nl.naturalis.nba.utils.convert.Stringifier;

/**
 * A {@code MappingInfo} object provides easy, programmatic access to various
 * aspects of an Elasticsearch {@link Mapping type mapping}. See
 * {@link MappingFactory} for more details.
 * 
 * @author Ayco Holleman
 *
 */
public class MappingInfo<T extends IDocumentObject> {

	private static final HashMap<Mapping<?>, HashMap<Path, ESField>> fieldCache = new HashMap<>(8);

	/**
	 * Determines if the specified field <i>or any of its ancestors</i> is a
	 * multi-valued field.
	 * 
	 * @see ESField#isArray()
	 * 
	 * @param field
	 * @return
	 */
	public static boolean isOrDescendsFromArray(ESField field)
	{
		for (ESField f = field; f != null; f = f.getParent()) {
			if (f.isArray()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the parent document and the parent document's ancestors of the
	 * specified field.
	 * 
	 * @param field
	 * @return
	 */
	public static List<ComplexField> getAncestors(ESField field)
	{
		List<ComplexField> ancestors = new ArrayList<>(3);
		field = field.getParent();
		while (field.getParent() != null) {
			ancestors.add((ComplexField) field);
			field = field.getParent();
		}
		return ancestors;
	}

	/**
	 * Returns a substring of the specified path up to, and including the
	 * <i>lowest level</i> object with type "nested" (rather than "object").
	 * This is the path that must be used for a nested query on the specified
	 * field. If this method returns {@code null}, it implicitly means no nested
	 * query is required.
	 * 
	 * @param field
	 * @return
	 */
	public static String getNestedPath(ESField field)
	{
		List<ComplexField> in = getAncestors(field);
		List<ComplexField> out;
		for (int i = 0; i < in.size(); ++i) {
			ComplexField d = in.get(i);
			if (d.getType() == NESTED) {
				out = in.subList(i, in.size());
				Collections.reverse(out);
				return CollectionUtil.implode(out, ".", new Stringifier<ComplexField>() {

					public String execute(ComplexField doc, Object... args)
					{
						return doc.getName();
					}
				});
			}
		}
		return null;
	}

	private final Mapping<T> mapping;

	public MappingInfo(Mapping<T> mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * Returns the {@link Mapping} object wrapped by this instance.
	 * 
	 * @return
	 */
	public Mapping<T> getMapping()
	{
		return mapping;
	}

	/**
	 * Returns an {@link ESField} instance corresponding to the path string.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public ESField getField(String path) throws NoSuchFieldException
	{
		return getField(new Path(path));
	}

	/**
	 * Returns an {@link ESField} instance corresponding to the specified
	 * {@link Path} object.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public ESField getField(Path path) throws NoSuchFieldException
	{
		ESField field = getFromCache(path);
		if (field == null) {
			LinkedHashMap<String, ESField> map = mapping.getProperties();
			field = getField(path, path, map);
			addToCache(path, field);
		}
		return field;
	}

	/**
	 * Returns the Elasticsearch data type of the specified field. Basically
	 * equivalent to {@link #getField(String) getField(field).getType()}.
	 * However, that method may return {@code null}, meaning that the type of
	 * the field is "{@link ESDataType#OBJECT object}". This method will never
	 * return never return {@code null}.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public ESDataType getType(String path) throws NoSuchFieldException
	{
		ESField f = getField(path);
		return f.getType() == null ? OBJECT : f.getType();
	}

	/**
	 * Returns the parent document and its ancestors of the specified field.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public List<ComplexField> getAncestors(String path) throws NoSuchFieldException
	{
		return getAncestors(getField(path));
	}

	/**
	 * Returns a substring of the specified path up to, and including the
	 * <i>lowest level</i> object with type "nested" (rather than "object").
	 * This is the path to be used for a nested query on the specified field. If
	 * this method returns {@code null}, it implicitly means no nested query is
	 * required.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public String getNestedPath(String path) throws NoSuchFieldException
	{
		return getNestedPath(getField(path));
	}

	/**
	 * Returns the full path of all fields within a document.
	 * 
	 * @param sorted
	 *            Whether or not to alphabetically sort the fields. If not they
	 *            are returned in the order as the appear in the document.
	 * @return
	 */
	public String[] getPathStrings(boolean sorted)
	{
		List<String> paths = new ArrayList<>(150);
		LinkedHashMap<String, ESField> properties = mapping.getProperties();
		for (Map.Entry<String, ESField> property : properties.entrySet()) {
			addPath(paths, null, property.getKey(), property.getValue());
		}
		String[] result = paths.toArray(new String[paths.size()]);
		if (sorted) {
			Arrays.sort(result);
		}
		return result;
	}

	private static void addPath(List<String> paths, String parent, String child, ESField field)
	{
		String path = parent == null ? child : parent + '.' + child;
		if (field instanceof SimpleField) {
			paths.add(path);
		}
		else {
			ComplexField cf = (ComplexField) field;
			LinkedHashMap<String, ESField> fields = cf.getProperties();
			for (Entry<String, ESField> e : fields.entrySet()) {
				addPath(paths, path, e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * See {@link #isOrDescendsFromArray(ESField)}.
	 * 
	 * @param path
	 * @return
	 * @throws NoSuchFieldException
	 */
	public boolean isOrDescendsFromArray(String path) throws NoSuchFieldException
	{
		return isOrDescendsFromArray(getField(path));
	}

	private ESField getField(Path fullPath, Path path, Map<String, ? extends ESField> map)
			throws NoSuchFieldException
	{
		ESField f = map.get(path.getElement(0));
		if (f == null || f instanceof MultiField) {
			// Prevent access to MultiField fields
			throw new NoSuchFieldException(fullPath, path.element(0));
		}
		if (path.countElements() == 1) {
			return f;
		}
		if (f instanceof ComplexField) {
			map = ((ComplexField) f).getProperties();
			return getField(fullPath, path.shift(), map);
		}
		throw new NoSuchFieldException(fullPath, path.element(0));
	}

	private ESField getFromCache(Path path)
	{
		HashMap<Path, ESField> myCache = fieldCache.get(mapping);
		if (myCache == null) {
			return null;
		}
		return myCache.get(path);
	}

	private void addToCache(Path path, ESField field)
	{
		HashMap<Path, ESField> myCache = fieldCache.get(mapping);
		if (myCache == null) {
			myCache = new HashMap<>();
			fieldCache.put(mapping, myCache);
		}
		myCache.put(path, field);
	}

}
