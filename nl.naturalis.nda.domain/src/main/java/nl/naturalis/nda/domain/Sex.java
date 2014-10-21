package nl.naturalis.nda.domain;

public enum Sex
{

	MALE("male"), FEMALE("female");

	public static Sex forName(String name)
	{
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (name.equalsIgnoreCase(MALE.name)) {
			return MALE;
		}
		if (name.equalsIgnoreCase(FEMALE.name)) {
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
