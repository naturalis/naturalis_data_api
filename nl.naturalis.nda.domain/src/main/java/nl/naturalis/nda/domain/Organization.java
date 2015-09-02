package nl.naturalis.nda.domain;

public class Organization extends Agent {

	private String name;

	public Organization()
	{
	}

	public Organization(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Organization)) {
			return false;
		}
		Organization other = (Organization) obj;
		return eq(name, other.name);
	}

	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + name == null ? 0 : name.hashCode();
		return hash;
	}

	public String toString()
	{
		return "{name: " + quote(name) + "}";
	}

	private static String quote(String s)
	{
		return s == null ? "null" : '"' + s + '"';
	}

	private static boolean eq(Object obj0, Object obj2)
	{
		if (obj0 == null) {
			if (obj2 == null) {
				return true;
			}
			return false;
		}
		return obj2 == null ? false : obj0.equals(obj2);
	}
}
