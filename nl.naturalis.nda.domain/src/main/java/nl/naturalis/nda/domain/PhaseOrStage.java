package nl.naturalis.nda.domain;

public enum PhaseOrStage
{

	ADULT, SUBADULT, EGG, EMBRYO, IMMATURE, JUVENILE, LARVA, NYMPH;

	public static PhaseOrStage parse(String name)
	{
		if (name == null) {
			return null;
		}
		for (PhaseOrStage pos : PhaseOrStage.values()) {
			if (pos.name.equalsIgnoreCase(name)) {
				return pos;
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
