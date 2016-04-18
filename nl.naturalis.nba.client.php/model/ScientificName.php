<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\TaxonomicStatus;

class ScientificName extends NBADomainObject {

	private $fullScientificName;
	private $taxonomicStatus;
	private $genusOrMonomial;
	private $subgenus;
	private $specificEpithet;
	private $infraspecificEpithet;
	private $infraspecificMarker;
	private $nameAddendum;
	private $authorshipVerbatim;
	private $author;
	private $year;
	private $references;
	private $experts;

	public function getFullScientificName()
	{
		return $this->fullScientificName;
	}

	public function setFullScientificName(string $fullScientificName)
	{
		$this->fullScientificName = $fullScientificName;
	}

	public function getTaxonomicStatus()
	{
		return $this->taxonomicStatus;
	}

	public function setTaxonomicStatus(TaxonomicStatus $taxonomicStatus)
	{
		$this->taxonomicStatus = $taxonomicStatus;
	}

	public function getGenusOrMonomial()
	{
		return $this->genusOrMonomial;
	}

	public function setGenusOrMonomial(string $genusOrMonomial)
	{
		$this->genusOrMonomial = $genusOrMonomial;
	}

	public function getSubgenus()
	{
		return $this->subgenus;
	}

	public function setSubgenus(string $subgenus)
	{
		$this->subgenus = $subgenus;
	}

	public function getSpecificEpithet()
	{
		return $this->specificEpithet;
	}

	public function setSpecificEpithet(string $specificEpithet)
	{
		$this->specificEpithet = $specificEpithet;
	}

	public function getInfraspecificEpithet()
	{
		return $this->infraspecificEpithet;
	}

	public function setInfraspecificEpithet(string $infraspecificEpithet)
	{
		$this->infraspecificEpithet = $infraspecificEpithet;
	}

	public function getInfraspecificMarker()
	{
		return $this->infraspecificMarker;
	}

	public function setInfraspecificMarker(string $infraspecificMarker)
	{
		$this->infraspecificMarker = $infraspecificMarker;
	}

	public function getNameAddendum()
	{
		return $this->nameAddendum;
	}

	public function setNameAddendum(string $nameAddendum)
	{
		$this->nameAddendum = $nameAddendum;
	}

	public function getAuthorshipVerbatim()
	{
		return $this->authorshipVerbatim;
	}

	public function setAuthorshipVerbatim(string $authorshipVerbatim)
	{
		$this->authorshipVerbatim = $authorshipVerbatim;
	}

	public function getAuthor()
	{
		return $this->author;
	}

	public function setAuthor(string $author)
	{
		$this->author = $author;
	}

	public function getYear()
	{
		return $this->year;
	}

	public function setYear(string $year)
	{
		$this->year = $year;
	}

	public function getReferences()
	{
		return $this->references;
	}

	public function setReferences(array $references)
	{
		$this->references = $references;
	}

	public function getExperts()
	{
		return $this->experts;
	}

	public function setExperts(array $experts)
	{
		$this->experts = $experts;
	}
}
