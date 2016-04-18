<?php

namespace nl\naturalis\nba\client\php\model;

use \DateTime;
use nl\naturalis\nba\client\php\model\Agent;
use nl\naturalis\nba\client\php\model\DefaultClassification;
use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\model\ScientificName;

abstract class TaxonomicIdentification extends NBADomainObject {

	private $taxonRank;
	private $scientificName;
	private $defaultClassification;
	private $systemClassification;
	private $vernacularNames;
	private $identificationQualifiers;
	private $dateIdentified;
	private $identifiers;

	public function __construct()
	{
		parent::__construct();
	}

	public function addIdentifier(Agent $identifier)
	{
		if ($this->identifiers == null) {
			$this->identifiers = array();
		}
		$this->identifiers[] = $identifier;
	}

	public function getTaxonRank()
	{
		return $this->taxonRank;
	}

	public function setTaxonRank(string $taxonRank)
	{
		$this->taxonRank = $taxonRank;
	}

	public function getScientificName()
	{
		return $this->scientificName;
	}

	public function setScientificName(ScientificName $scientificName)
	{
		$this->scientificName = $scientificName;
	}

	public function getDefaultClassification()
	{
		return $this->defaultClassification;
	}

	public function setDefaultClassification(DefaultClassification $defaultClassification)
	{
		$this->defaultClassification = $defaultClassification;
	}

	public function getSystemClassification()
	{
		return $this->systemClassification;
	}

	public function setSystemClassification(array $systemClassification)
	{
		$this->systemClassification = $systemClassification;
	}

	public function getVernacularNames()
	{
		return $this->vernacularNames;
	}

	public function setVernacularNames(array $vernacularNames)
	{
		$this->vernacularNames = $vernacularNames;
	}

	public function getIdentificationQualifiers()
	{
		return $this->identificationQualifiers;
	}

	public function setIdentificationQualifiers(array $identificationQualifiers)
	{
		$this->identificationQualifiers = $identificationQualifiers;
	}

	public function getDateIdentified()
	{
		return $this->dateIdentified;
	}

	public function setDateIdentified(DateTime $dateIdentified)
	{
		$this->dateIdentified = $dateIdentified;
	}

	public function getIdentifiers()
	{
		return $this->identifiers;
	}

	public function setIdentifiers(array $identifiers)
	{
		$this->identifiers = $identifiers;
	}
}
