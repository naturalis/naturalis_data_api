package nl.naturalis.nda.elasticsearch.load.normalize;

import org.domainobject.util.StringUtil;

public class ClasspathMappingFileNormalizer<T extends Enum<T>> extends Normalizer<T> {

	public ClasspathMappingFileNormalizer(Class<T> enumClass, String resource)
	{
		super(enumClass);
		loadMappings(StringUtil.getResourceAsString(resource));
	}

}
