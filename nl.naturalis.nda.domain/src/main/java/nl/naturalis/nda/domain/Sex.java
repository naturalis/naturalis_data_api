package nl.naturalis.nda.domain;

import java.util.Arrays;
import java.util.HashSet;

public enum Sex
{

	MALE("male"), FEMALE("female");

	private static final HashSet<String> maleStrings = new HashSet<String>(Arrays.asList(MALE.toString(), "m", "m."));
	private static final HashSet<String> femaleStrings = new HashSet<String>(Arrays.asList(FEMALE.toString(), "f", "f."));


	public static Sex forName(String name)
	{
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (maleStrings.contains(name)) {
			return MALE;
		}
		if (femaleStrings.contains(name)) {
			return FEMALE;
		}
		return null;
	}

	private final String name;


	private Sex(String name)
	{
		this.name = name;
	}


	public String toString()
	{
		return name;
	}

}
