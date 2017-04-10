package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Sex implements INbaModelObject
{

	MALE, FEMALE, MIXED, HERMAPHRODITE;

	@JsonCreator
	public static Sex parse(@JsonProperty("name") String name)
	{
		if (name == null) {
			return null;
		}
		for (Sex sex : Sex.values()) {
			if (sex.name.equalsIgnoreCase(name)) {
				return sex;
			}
		}
		throw new IllegalArgumentException("Invalid sex: " + name);
	}

	private final String name = name().toLowerCase();

	@JsonValue
	@Override
	public String toString()
	{
		return name;
	}

}
