package nl.naturalis.nba.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class SummarySourceSystem {

	private String code;

	@JsonCreator
	public SummarySourceSystem(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}

}
