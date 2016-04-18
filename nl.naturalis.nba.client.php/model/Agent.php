<?php

namespace nl\naturalis\nba\client\php\model;

class Agent extends NBADomainObject {

	private $agentText;

	public function __construct()
	{
	}

	public function __construct($agentText)
	{
		$this->agentText = $agentText;
	}

	public function getAgentText()
	{
		return $this->agentText;
	}

	public function setAgentText(string $agentText)
	{
		$this->agentText = $agentText;
	}
}
