package nl.naturalis.nda.domain;

/**
 * An enumeration of the three main domain objects within the NBA: specimens,
 * taxa and multimedia objects.
 * 
 * @author Ayco Holleman
 *
 */
public enum ObjectType
{

	SPECIMEN(), TAXON("taxa"), MULTIMEDIA("multimedia");

	public static ObjectType forName(String name)
	{
		try {
			return valueOf(name.toUpperCase());
		}
		catch (NullPointerException e) {
			if (name.equals(TAXON.plural)) {
				return TAXON;
			}
			return null;
		}
	}

	private final String singular;
	private final String plural;


	private ObjectType()
	{
		this.singular = name().toLowerCase();
		this.plural = singular + 's';
	}


	private ObjectType(String plural)
	{
		this.singular = name().toLowerCase();
		this.plural = plural;
	}


	/**
	 * Returns the singular form of the object type's name
	 */
	public String getSingular()
	{
		return singular;
	}


	/**
	 * Returns the plural form of the object type's name
	 */
	public String getPlural()
	{
		return plural;
	}


	/**
	 * Returns the singular form of the object type's name
	 */
	@Override
	public String toString()
	{
		return singular;
	}

}
