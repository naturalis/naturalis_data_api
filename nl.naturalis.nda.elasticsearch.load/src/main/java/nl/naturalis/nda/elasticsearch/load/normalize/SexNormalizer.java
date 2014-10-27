package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nda.domain.Sex;

public class SexNormalizer extends ClasspathMappingFileNormalizer<Sex> {

	private static final String RESOURCE = "/normalize/sex.csv";

	private static SexNormalizer instance;


	public static SexNormalizer getInstance()
	{
		if (instance == null) {
			instance = new SexNormalizer();
		}
		return instance;
	}


	private SexNormalizer()
	{
		super(Sex.class, RESOURCE);
	}

}
