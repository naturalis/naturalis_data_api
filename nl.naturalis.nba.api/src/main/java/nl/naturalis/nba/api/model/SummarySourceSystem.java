package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
