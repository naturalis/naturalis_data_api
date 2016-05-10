package nl.naturalis.nba.dao.es.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.domainobject.util.debug.BeanPrinter;

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
		MappingInspector mi = cache.get(type);
		if (mi == null) {
			MappingFactory mf = new MappingFactory();
			Mapping mapping = mf.getMapping(type);
			BeanPrinter.out(mapping);
			mi = new MappingInspector(mapping);
			cache.put(type, mi);
		}
		return mi;
	}

	private final Mapping mapping;

	private MappingInspector(Mapping mapping)
	{
		this.mapping = mapping;
	}

	/**
	 * Returns the {@link ESField} instance corresponding to the specified path.
	 * 
	 * @param path
	 * @return
	 */
	public ESField getField(String path)
	{
		LinkedHashMap<String, ESField> map = mapping.getProperties();
		String[] chunks = path.split("\\.");
		List<String> pathElements = Arrays.asList(chunks);
		ESField f = getField(pathElements, map);
		if (f == null) {
			throw new NoSuchFieldException(path);
		}
		return f;
	}

	/**
	 * Returns the Elasticsearch data type of the specified field. Basically
	 * equivalent to {@link #getField(String) getField(field).getType()}.
	 * However, if {@code getType()} returns {@code null}, this method will
	 * return the default data type ("object") instead.
	 * 
	 * @param field
	 * @return
	 */
	public ESDataType getType(String field)
	{
		ESField f = getField(field);
		return f.getType() == null ? ESDataType.OBJECT : f.getType();
	}

	/**
	 * Returns the parent document and its ancestors of the specified field.
	 * 
	 * @param field
	 * @return
	 */
	public List<Document> getAncestors(String field)
	{
		LinkedHashMap<String, ESField> map = mapping.getProperties();
		String[] chunks = field.split("\\.");
		List<String> path = Arrays.asList(chunks);
		List<Document> ancestors = getAncestors(new ArrayList<Document>(4), path, map);
		if (ancestors == null) {
			throw new NoSuchFieldException(field);
		}
		return ancestors;
	}

	private ESField getField(List<String> path, Map<String, ? extends ESField> map)
	{
		ESField f = map.get(path.get(0));
		if (f == null) {
			return null;
		}
		if (f instanceof DocumentField || path.size() == 1) {
			return f;
		}
		path = path.subList(1, path.size());
		map = ((Document) f).getProperties();
		return getField(path, map);
	}

	private List<Document> getAncestors(List<Document> ancestors, List<String> path,
			Map<String, ? extends ESField> map)
	{
		ESField f = map.get(path.get(0));
		if (f == null) {
			return null;
		}
		if (path.size() == 1) {
			return ancestors;
		}
		ancestors.add((Document) f);
		path = path.subList(1, path.size());
		map = ((Document) f).getProperties();
		return getAncestors(ancestors, path, map);
	}

}
