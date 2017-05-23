package nl.naturalis.nba.api.model.summary;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.naturalis.nba.api.model.SourceSystem;

/**
 * A miniature version of {@link SourceSystem}.
 * 
 * @author Ayco Holleman
 *
 */
public class SummarySourceSystem {

	private String code;

	@JsonCreator
	public SummarySourceSystem(@JsonProperty("code") String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}

}
