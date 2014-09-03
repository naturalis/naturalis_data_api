package nl.naturalis.nda.domain;

public class GatheringAgent extends NdaDomainObject {

	private String agentText;


	public GatheringAgent()
	{
	}


	public GatheringAgent(String agentText)
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
