package nl.naturalis.nda.elasticsearch.load.normalize;

import java.io.File;

public class ClasspathMappingFileNormalizer<T extends Enum<T>> extends Normalizer<T> {

	public ClasspathMappingFileNormalizer(Class<T> enumClass, String resource)
	{
		super(enumClass);
		String path = getClass().getResource(resource).getFile();
		File file = new File(path);
		loadMappings(file);
	}

}
