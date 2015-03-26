package nl.naturalis.nda.domain;

public enum SpecimenTypeStatus
{

	//@formatter:off
	ALLOTYPE("allotype"),
	EPITYPE("epitype"),
	HAPANTOTYPE("hapantotype"),
	HOLOTYPE("holotype"),
	ISOEPITYPE("isoepitype"),
	ISOLECTOTYPE("isolectotype"),
	ISONEOTYPE("isoneotype"),
	ISOSYNTYPE("isosyntype"),
	ISOTYPE("isotype"),
	LECTOTYPE("lectotype"),
	NEOTYPE("neotype"),
	PARATYPE("paratype"),
	PARALECTOTYPE("paralectotype"),
	SYNTYPE("syntype"),
	TOPOTYPE("topotype"),
	/**
	 * A type status assigned to a specimen if it is not known whether it's a
	 * holotype, paratype, lectotype, etc., but it is known to be one of those.
	 */
	TYPE("type");
	//@formatter:on

	public static SpecimenTypeStatus forName(String name)
	{
		if (name != null) {
			for (SpecimenTypeStatus s : SpecimenTypeStatus.values()) {
				if (s.name.equals(name)) {
					return s;
				}
			}
		}
		return null;
	}

	private final String name;


	private SpecimenTypeStatus(String name)
	{
		this.name = name;
	}


	public String toString()
	{
		return name;
	}
}
