package nl.naturalis.nba.api;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SortOrder
{
	ASC, DESC;

	@JsonCreator
	public static SortOrder parse(String name)
	{
		if (name == null || name.isEmpty()) {
			return ASC;
		}
		if (name.toUpperCase().equals(ASC.name())) {
			return ASC;
		}
		if (name.toUpperCase().equals(DESC.name())) {
			return DESC;
		}
		throw new IllegalArgumentException("No such sort order: " + name);
	}

}
