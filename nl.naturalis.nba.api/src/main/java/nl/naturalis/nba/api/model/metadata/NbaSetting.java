package nl.naturalis.nba.api.model.metadata;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.QuerySpec;

/**
 * Enumerates all publicly available configuration settings for the NBA.
 * 
 * @author Ayco Holleman
 *
 */
public enum NbaSetting
{
	/**
	 * &#34;operator.CONTAINS.min.term.length&#34;. The minimum length of a
	 * search term when using operator {@link ComparisonOperator#CONTAINS} or
	 * {@link ComparisonOperator#NOT_CONTAINS}.
	 */
	OPERATOR_CONTAINS_MIN_TERM_LENGTH("operator.contains.min_term_length"),
	/**
	 * &#34;operator.CONTAINS.max.term.length&#34;. The maximum length of a
	 * search term when using operator {@link ComparisonOperator#CONTAINS} or
	 * {@link ComparisonOperator#NOT_CONTAINS}.
	 */
	OPERATOR_CONTAINS_MAX_TERM_LENGTH("operator.contains.max_term_length"),

	/**
	 * &#34;index.max_result_window&#34;. The size of the result window. When
	 * specifying an NBA query using a {@link QuerySpec}, the sum of
	 * {@link QuerySpec#getFrom() from} plus {@link QuerySpec#getSize()} must
	 * never exceed this number.
	 */
	INDEX_MAX_RESULT_WINDOW("index.max_result_window"),

	/**
	 * &#34;taxon.group_by_scientific_name.max_num_buckets&#34;. The maximum
	 * number of unique scientific names returned by the groupByScientificName
	 * service for taxa.
	 */
	TAXON_GROUP_BY_SCIENTIFIC_NAME_MAX_NUM_BUCKETS("taxon.group_by_scientific_name.max_num_buckets"),

	/**
	 * &#34;specimen.group_by_scientific_name.max_num_buckets&#34;. The maximum
	 * number of unique scientific names returned by the groupByScientificName
	 * service for specimens.
	 */
	SPECIMEN_GROUP_BY_SCIENTIFIC_NAME_MAX_NUM_BUCKETS("specimen.group_by_scientific_name.max_num_buckets");

	@JsonCreator
	public static NbaSetting parse(@JsonProperty("name") String name)
	{
		if (name == null) {
			return null;
		}
		for (NbaSetting setting : values()) {
			if (name.equalsIgnoreCase(setting.name)) {
				return setting;
			}
		}
		throw new IllegalArgumentException("Invalid NBA setting: " + name);
	}

	private String name;

	private NbaSetting(String name)
	{
		this.name = name;
	}

	@JsonValue
	@Override
	public String toString()
	{
		return name;
	}

}
