package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxonomicStatus implements INbaModelObject
{

	ACCEPTED_NAME("accepted name"),
	SYNONYM,
	BASIONYM,
	HOMONYM,
	AMBIGUOUS_SYNONYM("ambiguous synonym"),
	MISAPPLIED_NAME("misapplied name"),
	MISSPELLED_NAME("misspelled name"),
	PROVISIONALLY_ACCEPTED("provisionally accepted name");

	@JsonCreator
	public static TaxonomicStatus parse(@JsonProperty("name") String name)
	{
		if (name == null) {
			return null;
		}
		for (TaxonomicStatus status : TaxonomicStatus.values()) {
			if (status.name.equalsIgnoreCase(name)) {
				return status;
			}
		}
		throw new IllegalArgumentException("Invalid taxonomic status: " + name);
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

	@JsonValue
	public String toString()
	{
		return name;
	}

}