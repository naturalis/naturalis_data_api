package nl.naturalis.nda.domain;

public class Agent extends NdaDomainObject {

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
