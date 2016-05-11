package nl.naturalis.nba.dao.es.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.domainobject.util.CollectionUtil;
import org.domainobject.util.convert.Stringifier;

import nl.naturalis.nba.dao.es.types.ESType;

/**
 * A {@code MappingInspector} provides easy, programmatic access to various
 * aspects of an Elasticsearch mapping.
 * 
 * @author Ayco Holleman
 *
 */
public class MappingInspector {

	private static final HashMap<Class<? extends ESType>, MappingInspector> cache = new HashMap<>();

	/**
	 * Returns a {@link MappingInspector} for the specified Elasticsearch type.
	 * 
	 * @param type
	 * @return
	 */
	public static MappingInspector forType(Class<? extends ESType> type)
	{
		MappingInspector inspector = cache.get(type);
		if (inspector == null) {
			MappingFactory mf = new MappingFactory();
			Mapping mapping = mf.getMapping(type);
			inspector = new MappingInspector(mapping);
			cache.put(type, inspector);
		}
		return inspector;
	}

	private final Mapping mapping;

	private MappingInspector(Mapping mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * Returns the {@link ESField} instance corresponding to the path string.
	 * 
	 * @param path
	 * @return
	 */
	public ESField getField(String path)
	{
		LinkedHashMap<String, ESField> map = mapping.getProperties();
		String[] chunks = path.split("\\.");
		List<String> pathElements = Arrays.asList(chunks);
		return getField(path, pathElements, map);
	}

	/**
	 * Returns the Elasticsearch data type of the specified field. Basically
	 * equivalent to {@link #getField(String) getField(field).getType()}.
	 * However, if {@code getType()} returns {@code null}, this method will
	 * return the appropriate default data type instead ("string" for fields;
	 * "object" for nested documents).
	 * 
	 * @param path
	 * @return
	 */
	public ESDataType getType(String path)
	{
		ESField f = getField(path);
		return f.getType() == null ? ESDataType.OBJECT : f.getType();
	}

	/**
	 * Returns the parent document and its ancestors of the specified field.
	 * 
	 * @param path
	 * @return
	 */
	public List<Document> getAncestors(String path)
	{
		return getAncestors(getField(path));
	}

	/**
	 * Returns the parent document and its ancestors of the specified field.
	 * 
	 * @param f
	 * @return
	 */
	public List<Document> getAncestors(ESField f)
	{
		List<Document> ancestors = new ArrayList<>(3);
		f = f.getParent();
		while (f.getParent() != null) {
			ancestors.add((Document) f);
			f = f.getParent();
		}
		return ancestors;
	}

	/**
	 * Returns a substring of the specified path up to, and including the
	 * <i>lowest level</i> object with type "nested" (rather than "object").
	 * This is the path to be used for a nested query on the specified field. If
	 * this method returns {@code null}, this implicitly means no nested query
	 * is required.
	 * 
	 * @param path
	 * @return
	 */
	public String getNestedPath(String path)
	{
		return getNestedPath(getField(path));
	}

	/**
	 * Returns a substring of the specified path up to, and including the
	 * <i>lowest level</i> object with type "nested" (rather than "object").
	 * This is the path to be used for a nested query on the specified field. If
	 * this method returns {@code null}, this implicitly means no nested query
	 * is required.
	 * 
	 * @param field
	 * @return
	 */
	public String getNestedPath(ESField field)
	{
		List<Document> in = getAncestors(field);
		List<Document> out;
		for (int i = 0; i < in.size(); ++i) {
			Document d = in.get(i);
			if (d.getType() == ESDataType.NESTED) {
				out = in.subList(i, in.size());
				Collections.reverse(out);
				return CollectionUtil.implode(out, ".", new Stringifier<Document>() {

					public String execute(Document doc, Object... args)
					{
						return doc.getName();
					}
				});
			}
		}
		return null;
	}

	private ESField getField(String origPath, List<String> path, Map<String, ? extends ESField> map)
	{
		ESField f = map.get(path.get(0));
		if (f == null || f instanceof MultiField) {
			// Prevent access to MultiField fields
			throw new NoSuchFieldException(origPath);
		}
		if (path.size() == 1) {
			return f;
		}
		if (f instanceof Document) {
			path = path.subList(1, path.size());
			map = ((Document) f).getProperties();
			return getField(origPath, path, map);
		}
		throw new NoSuchFieldException(origPath);
	}

}
