package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nda.domain.SpecimenTypeStatus;

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
