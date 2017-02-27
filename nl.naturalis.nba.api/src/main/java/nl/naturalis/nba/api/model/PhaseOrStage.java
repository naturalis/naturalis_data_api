package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PhaseOrStage implements INbaModelObject
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
