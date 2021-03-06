package nl.naturalis.nba.etl.normalize;

import nl.naturalis.nba.api.model.Sex;

/**
 * Normalizes different names for the various sexes. For example "F", "fem", and
 * "female" all map to "female".
 * 
 * @author Ayco Holleman
 *
 */
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
