package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SpecimenTypeStatus implements INbaModelObject
{

	ALLOTYPE,
	EPITYPE,
	HAPANTOTYPE,
	HOLOTYPE,
	ISOEPITYPE,
	ISOLECTOTYPE,
	ISONEOTYPE,
	ISOSYNTYPE,
	ISOTYPE,
	LECTOTYPE,
	NEOTYPE,
	PARATYPE,
	PARALECTOTYPE,
	SYNTYPE,
	TOPOTYPE,
	/**
	 * A type status assigned to a specimen if it is not known whether it's a
	 * holotype, paratype, lectotype, etc., but it is known to be one of those.
	 */
	TYPE;

	@JsonCreator
	public static SpecimenTypeStatus parse(@JsonProperty("name") String name)
	{
		if (name == null) {
			return null;
		}
		for (SpecimenTypeStatus s : SpecimenTypeStatus.values()) {
			if (s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		throw new IllegalArgumentException("Invalid type status: " + name);
	}

	private final String name = name().toLowerCase();

	@JsonValue
	public String toString()
	{
		return name;
	}
}
