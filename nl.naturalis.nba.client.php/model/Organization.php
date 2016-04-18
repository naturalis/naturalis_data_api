<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\Agent;

class Organization extends Agent {

	private $name;

	public function __construct(string $name)
	{
		$this->name = $name;
	}

	public function getName()
	{
		return name;
	}

	public function setName(string $name)
	{
		$this->name = name;
	}

}
