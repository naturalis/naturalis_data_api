package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TaxonomicStatus implements INbaModelObject
{
	ACCEPTED_NAME("accepted name"),
	ALTERNATIVE_NAME("alternative name"),
	AMBIGUOUS_SYNONYM("ambiguous synonym"),
	BASIONYM("basionym"),
	HOMONYM("homonym"),
	INVALID_NAME("invalid name"),
	MISAPPLIED_NAME("misapplied name"),
	MISIDENTIFICATION("misidentification"),
	MISSPELLED_NAME("misspelled name"),
	NOMEN_NUDUM("nomen nudum"),
	PREFERRED_NAME("preferred name"),
	PROVISIONALLY_ACCEPTED("provisionally accepted name"),
  SYNONYM("synonym");

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