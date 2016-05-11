package nl.naturalis.nba.dao.es.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	 * return the default data type ("object") instead.
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
		ESField f = getField(path).getParent();
		List<Document> ancestors = new ArrayList<>(3);
		while (f.getParent() != null) {
			ancestors.add((Document) f);
			f = f.getParent();
		}
		return ancestors;
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
