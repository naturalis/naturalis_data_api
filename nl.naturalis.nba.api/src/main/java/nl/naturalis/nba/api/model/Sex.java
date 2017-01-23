package nl.naturalis.nba.api.model;

import nl.naturalis.nba.api.annotations.Analyzers;

public enum Sex implements INbaModelObject
{

	MALE, FEMALE, MIXED, HERMAPHRODITE;

	public static Sex parse(String name)
	{
		if (name == null) {
			return null;
		}
		for (Sex sex : Sex.values()) {
			if (sex.name.equalsIgnoreCase(name)) {
				return sex;
			}
		}
		return null;
	}

	@Analyzers({})
	private final String name;


	private Sex(String name)
	{
		this.name = name;
	}


	private Sex()
	{
		this.name = this.name().toLowerCase();
	}


	public String toString()
	{
		return name;
	}

}
