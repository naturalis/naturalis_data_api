<?php

namespace nl\naturalis\nba\client\php\model;

class DefaultClassification extends NBADomainObject {
	private $kingdom;
	private $phylum;
	private $className;
	private $order;
	private $superFamily;
	private $family;
	private $genus;
	private $subgenus;
	private $specificEpithet;
	private $infraspecificEpithet;
	private $infraspecificRank;

	public function __construct()
	{
		parent::__construct();
	}

	public function getKingdom()
	{
		return $this->kingdom;
	}

	public function setKingdom(string $kingdom)
	{
		$this->kingdom = kingdom;
	}

	public function getPhylum()
	{
		return $this->phylum;
	}

	public function setPhylum(string $phylum)
	{
		$this->phylum = phylum;
	}

	public function getClassName()
	{
		return $this->className;
	}

	public function setClassName(string $className)
	{
		$this->className = className;
	}

	public function getOrder()
	{
		return $this->order;
	}

	public function setOrder(string $order)
	{
		$this->order = order;
	}

	public function getSuperFamily()
	{
		return $this->superFamily;
	}

	public function setSuperFamily(string $superFamily)
	{
		$this->superFamily = superFamily;
	}

	public function getFamily()
	{
		return $this->family;
	}

	public function setFamily(string $family)
	{
		$this->family = family;
	}

	public function getGenus()
	{
		return $this->genus;
	}

	public function setGenus(string $genus)
	{
		$this->genus = genus;
	}

	public function getSubgenus()
	{
		return $this->subgenus;
	}

	public function setSubgenus(string $subgenus)
	{
		$this->subgenus = subgenus;
	}

	public function getSpecificEpithet()
	{
		return $this->specificEpithet;
	}

	public function setSpecificEpithet(string $specificEpithet)
	{
		$this->specificEpithet = specificEpithet;
	}

	public function getInfraspecificEpithet()
	{
		return $this->infraspecificEpithet;
	}

	public function setInfraspecificEpithet(string $infraspecificEpithet)
	{
		$this->infraspecificEpithet = infraspecificEpithet;
	}

	public function getInfraspecificRank()
	{
		return $this->infraspecificRank;
	}

	public function setInfraspecificRank(string $infraspecificRank)
	{
		$this->infraspecificRank = infraspecificRank;
	}
}
