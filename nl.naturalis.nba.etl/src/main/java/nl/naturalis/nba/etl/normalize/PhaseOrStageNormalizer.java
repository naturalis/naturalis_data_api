package nl.naturalis.nba.etl.normalize;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import nl.naturalis.nba.api.model.INbaModelObject;

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

enum PhaseOrStage implements INbaModelObject
{

  ADULT, SUBADULT, EGG, EMBRYO, IMMATURE, JUVENILE, LARVA, PUPA, NYMPH;

  @JsonCreator
  public static PhaseOrStage parse(@JsonProperty("name") String name)
  {
    if (name == null) {
      return null;
    }
    for (PhaseOrStage pos : PhaseOrStage.values()) {
      if (pos.name.equalsIgnoreCase(name)) {
        return pos;
      }
    }
    throw new IllegalArgumentException("Invalid phase or stage: " + name);
  }

  private final String name = name().toLowerCase();

  @JsonValue
  public String toString()
  {
    return name;
  }

}

