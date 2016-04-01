package nl.naturalis.nba.etl.normalize;

import nl.naturalis.nba.api.model.PhaseOrStage;

/**
 * Normalizes different names for the various life stages.
 * 
 * @author Ayco Holleman
 *
 */
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