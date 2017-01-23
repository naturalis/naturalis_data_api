package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.annotations.Analyzers;

public enum PhaseOrStage implements INbaModelObject
{

	ADULT, SUBADULT, EGG, EMBRYO, IMMATURE, JUVENILE, LARVA, PUPA, NYMPH;

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

	@Analyzers({})
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
