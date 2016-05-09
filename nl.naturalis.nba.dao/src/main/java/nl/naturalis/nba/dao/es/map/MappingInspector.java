package nl.naturalis.nba.dao.es.map;

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
	 * @param type
	 * @return
	 */
	public static MappingInspector forType(Class<? extends ESType> type)
	{
		MappingInspector mi = cache.get(type);
		if (mi == null) {
			MappingFactory mf = new MappingFactory();
			Mapping mapping = mf.getMapping(type);
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

	public ESDataType getType(String field)
	{
		LinkedHashMap<String, ESField> map = mapping.getProperties();
		String[] chunks = field.split("\\.");
		List<String> path = Arrays.asList(chunks);
		ESDataType type = getType(path, map);
		if (type == null) {
			throw new NoSuchFieldException(field);
		}
		return type;
	}

	private ESDataType getType(List<String> path, Map<String, ? extends ESField> map)
	{
		ESField f = map.get(path.get(0));
		if (f == null) {
			return null;
		}
		if (f instanceof Document) {
			if (path.size() == 1) {
				return f.getType() == null ? ESDataType.OBJECT : f.getType();
			}
			path = path.subList(1, path.size());
			map = ((Document) f).getProperties();
			return getType(path, map);
		}
		return f.getType();
	}

}
