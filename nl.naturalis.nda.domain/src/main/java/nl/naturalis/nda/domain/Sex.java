package nl.naturalis.nda.domain;

public enum Sex
{

	MALE, FEMALE, MIXED, HERMAPHRODITE;

	public static Sex forName(String name)
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
