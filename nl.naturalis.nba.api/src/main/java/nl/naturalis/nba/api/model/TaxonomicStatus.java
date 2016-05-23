package nl.naturalis.nba.api.model;

public enum TaxonomicStatus
{

	//@formatter:off	
    ACCEPTED_NAME("accepted name"),
    SYNONYM,
    BASIONYM,
    HOMONYM,
    AMBIGUOUS_SYNONYM("ambiguous synonym"),
    MISAPPLIED_NAME("misapplied name"),
    MISSPELLED_NAME("misspelled name"),
    PROVISIONALLY_ACCEPTED("provisionally accepted name");
	//@formatter:on

	public static TaxonomicStatus parse(String name)
	{
		if (name == null) {
			return null;
		}
		for (TaxonomicStatus status : TaxonomicStatus.values()) {
			if (status.name.equalsIgnoreCase(name)) {
				return status;
			}
		}
		return null;
	}

	private final String name;

	private TaxonomicStatus(String name)
	{
		this.name = name;
	}

	private TaxonomicStatus()
	{
		this.name = name().toLowerCase();
	}

	public String toString()
	{
		return name;
	}

}