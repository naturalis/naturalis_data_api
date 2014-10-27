package nl.naturalis.nda.domain;

public enum PhaseOrStage
{

	ADULT, SUBADULT, EGG, EMBRYO, IMMATURE, JUVENILE, LARVA, NYPMH;

	public static PhaseOrStage forName(String name)
	{
		if (name == null) {
			return null;
		}
		for (PhaseOrStage phaseOrStage : PhaseOrStage.values()) {
			if (phaseOrStage.name.equals(name)) {
				return phaseOrStage;
			}
		}
		return null;
	}

	private final String name;


	private PhaseOrStage(String name)
	{
		this.name = name;
	}


	private PhaseOrStage()
	{
		this.name = name().toLowerCase();
	}


	public String toString()
	{
		return name;
	}

}
