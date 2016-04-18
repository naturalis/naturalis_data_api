<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\Agent;
use \nl\naturalis\nba\client\php\model\Organization;

class Person extends Agent {

	private $fullName;
	private $organization;

	public function __construct($fullName)
	{
		$this->fullName = $fullName;
	}

	public function getFullName()
	{
		return fullName;
	}

	public function setFullName(string $fullName)
	{
		$this->fullName = $fullName;
	}

	public function getOrganization()
	{
		return organization;
	}

	public function setOrganization(Organization $organization)
	{
		$this->organization = $organization;
	}
}
