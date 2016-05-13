package nl.naturalis.nba.api.model;

public class Agent extends NBADomainObject {

	private String agentText;

	public Agent()
	{
	}

	public Agent(String agentText)
	{
		this.agentText = agentText;
	}

	public String getAgentText()
	{
		return agentText;
	}

	public void setAgentText(String agentText)
	{
		this.agentText = agentText;
	}
}
