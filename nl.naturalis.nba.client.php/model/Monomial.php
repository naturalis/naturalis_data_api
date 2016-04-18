<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;

class Monomial extends NBADomainObject {

	private $rank;
	private $name;

	public function __construct(string $rank, string $name)
	{
		parent::__construct();
		$this->rank = $rank;
		$this->name = $name;
	}

	public function getRank()
	{
		return $this->rank;
	}

	public function setRank(string $rank)
	{
		$this->rank = $rank;
	}

	public function getName()
	{
		return $this->name;
	}

	public function setName(string $name)
	{
		$this->name = $name;
	}
}
