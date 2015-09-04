package nl.naturalis.nda.domain;

public class Person extends Agent {

	private String fullName;
	private Organization organization;

	public Person()
	{
	}

	public Person(String fullName)
	{
		this.fullName = fullName;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public Organization getOrganization()
	{
		return organization;
	}

	public void setOrganization(Organization organization)
	{
		this.organization = organization;
	}

	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Person)) {
			return false;
		}
		Person other = (Person) obj;
		return eq(fullName, other.fullName) && eq(organization, other.organization);
	}

	public int hashCode()
	{
		int hash = 17;
		hash = (hash * 31) + (fullName == null ? 0 : fullName.hashCode());
		hash = (hash * 31) + (organization == null ? 0 : organization.hashCode());
		return hash;
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder(50);
		sb.append("{fullName: ").append(quote(fullName));
		sb.append(", organization: ").append(organization);
		sb.append("}");
		return sb.toString();
	}

	private static String quote(Object obj)
	{
		return obj == null ? "null" : '"' + String.valueOf(obj) + '"';
	}

	private static boolean eq(Object obj0, Object obj1)
	{
		if (obj0 == null) {
			if (obj1 == null) {
				return true;
			}
			return false;
		}
		return obj1 == null ? false : obj0.equals(obj1);
	}
}
