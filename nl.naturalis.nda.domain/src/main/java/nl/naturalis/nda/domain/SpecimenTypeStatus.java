package nl.naturalis.nda.domain;

import java.util.HashMap;

public class SpecimenTypeStatus {

	public static final SpecimenTypeStatus HOLOTYPE = new SpecimenTypeStatus("holotype");
	public static final SpecimenTypeStatus PARATYPE = new SpecimenTypeStatus("paratype");
	public static final SpecimenTypeStatus LECTOTYPE = new SpecimenTypeStatus("lectotype");
	public static final SpecimenTypeStatus PARALECTOTYPE = new SpecimenTypeStatus("paralectotype");
	public static final SpecimenTypeStatus SYNTYPE = new SpecimenTypeStatus("syntype");
	public static final SpecimenTypeStatus HAPANTOTYPE = new SpecimenTypeStatus("hapantotype");
	public static final SpecimenTypeStatus NEOTYPE = new SpecimenTypeStatus("neotype");
	public static final SpecimenTypeStatus ISOTYPE = new SpecimenTypeStatus("isotype");
	public static final SpecimenTypeStatus ISOLECTOTYPE = new SpecimenTypeStatus("isolectotype");

	/**
	 * A type status assigned to a specimen if it is not known whether it's a
	 * holotype, paratype, lectotype, etc., but it is known to be one of those.
	 */
	public static final SpecimenTypeStatus TYPE = new SpecimenTypeStatus("Type");

	private static HashMap<String, SpecimenTypeStatus> registry = new HashMap<String, SpecimenTypeStatus>();

	static {
		registry.put(HOLOTYPE.name.toLowerCase(), HOLOTYPE);
		registry.put(PARATYPE.name.toLowerCase(), PARATYPE);
		registry.put(LECTOTYPE.name.toLowerCase(), LECTOTYPE);
		registry.put(PARALECTOTYPE.name.toLowerCase(), PARALECTOTYPE);
	}


	public static SpecimenTypeStatus forName(String name)
	{
		if (name != null) {
			SpecimenTypeStatus status = registry.get(name.toLowerCase());
			if (status == null) {
				status = new SpecimenTypeStatus(name);
				registry.put(name.toLowerCase(), status);
			}
			return status;
		}
		return null;
	}

	private final String name;


	private SpecimenTypeStatus(String name)
	{
		this.name = name;
	}


	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof SpecimenTypeStatus)) {
			return false;
		}
		return name.equalsIgnoreCase(((SpecimenTypeStatus) obj).name);
	}


	public int hashCode()
	{
		return name.toLowerCase().hashCode();
	}


	public String toString()
	{
		return name;
	}
}
