<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\ScientificName;

class Synonym extends NBADomainObject {

	private $scientificName;
	private $taxa;

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\ScientificName
	 */
	public function getScientificName()
	{
		return $this->scientificName;
	}

	public function setScientificName(ScientificName $scientificName)
	{
		$this->scientificName = $scientificName;
	}

	public function getTaxa()
	{
		return $this->taxa;
	}

	public function setTaxa(array $taxa)
	{
		$this->taxa = taxa;
	}
}
