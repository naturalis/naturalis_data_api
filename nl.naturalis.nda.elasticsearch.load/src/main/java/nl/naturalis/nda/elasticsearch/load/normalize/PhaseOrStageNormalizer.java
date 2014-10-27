package nl.naturalis.nda.elasticsearch.load.normalize;

import nl.naturalis.nda.domain.PhaseOrStage;

public class PhaseOrStageNormalizer extends ClasspathMappingFileNormalizer<PhaseOrStage> {

	private static final String RESOURCE = "/normalize/phase-or-stage.csv";

	private static PhaseOrStageNormalizer instance;


	public static PhaseOrStageNormalizer getInstance()
	{
		if (instance == null) {
			instance = new PhaseOrStageNormalizer();
		}
		return instance;
	}


	private PhaseOrStageNormalizer()
	{
		super(PhaseOrStage.class, RESOURCE);
	}

}
