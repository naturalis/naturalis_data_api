package nl.naturalis.nda.elasticsearch.load.normalize;

import org.domainobject.util.StringUtil;

/**
 * A subclass of {@link Normalizer} that maps values using a mapping file
 * presumed to be located somewhere on the classpath.
 * 
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            An {@code enum} class that maintains the canonical values. That
 *            is, calling {@code toString()} on any of the enum's constants
 *            yields a canonical value.
 */
public class ClasspathMappingFileNormalizer<T extends Enum<T>> extends Normalizer<T> {

	public ClasspathMappingFileNormalizer(Class<T> enumClass, String resource)
	{
		super(enumClass);
		loadMappings(StringUtil.getResourceAsString(resource));
	}

}
