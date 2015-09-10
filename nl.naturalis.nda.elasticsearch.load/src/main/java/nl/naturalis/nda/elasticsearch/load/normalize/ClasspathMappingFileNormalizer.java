package nl.naturalis.nda.elasticsearch.load.normalize;

import org.domainobject.util.StringUtil;

/**
 * A subclass of {@link Normalizer} that uses a mapping file in the classpath
 * for mapping values to their canonical equivalents.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 */
public class ClasspathMappingFileNormalizer<T extends Enum<T>> extends Normalizer<T> {

	public ClasspathMappingFileNormalizer(Class<T> enumClass, String resource)
	{
		super(enumClass);
		loadMappings(StringUtil.getResourceAsString(resource));
	}

}
