package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nba.api.model.SpecimenTypeStatus;

/**
 * Normalizer different names for the various specimen type statuses.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenTypeStatusNormalizer extends ClasspathMappingFileNormalizer<SpecimenTypeStatus> {

	private static final String RESOURCE = "/normalize/specimen-type-status.csv";

	private static SpecimenTypeStatusNormalizer instance;

	public static SpecimenTypeStatusNormalizer getInstance()
	{
		if (instance == null) {
			instance = new SpecimenTypeStatusNormalizer();
		}
		return instance;
	}

	private SpecimenTypeStatusNormalizer()
	{
		super(SpecimenTypeStatus.class, RESOURCE);
	}

}
