package nl.naturalis.nba.api.model;

public class Agent implements INbaModelObject {

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
