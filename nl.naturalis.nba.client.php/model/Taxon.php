<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\helper\ExpertList;
use nl\naturalis\nba\client\php\helper\MonomialList;
use nl\naturalis\nba\client\php\helper\ReferenceList;
use nl\naturalis\nba\client\php\helper\SpecimenList;
use nl\naturalis\nba\client\php\helper\SynonymList;
use nl\naturalis\nba\client\php\helper\TaxonDescriptionList;
use nl\naturalis\nba\client\php\helper\VernacularNameList;
use nl\naturalis\nba\client\php\model\DefaultClassification;
use nl\naturalis\nba\client\php\model\NBATraceableObject;
use nl\naturalis\nba\client\php\model\ScientificName;

class Taxon extends NBATraceableObject {

	private $sourceSystemParentId;
	private $taxonRank;
	private $acceptedName;
	private $defaultClassification;
	private $systemClassification;
	private $synonyms;
	private $vernacularNames;
	private $descriptions;
	private $references;
	private $experts;
	private $specimens;

	/**
	 *
	 * @return string
	 */
	public function getSourceSystemParentId()
	{
		return $this->sourceSystemParentId;
	}

	public function setSourceSystemParentId(string $sourceSystemParentId)
	{
		$this->sourceSystemParentId = sourceSystemParentId;
	}

	/**
	 *
	 * @return string
	 */
	public function getTaxonRank()
	{
		return $this->taxonRank;
	}

	public function setTaxonRank(string $taxonRank)
	{
		$this->taxonRank = taxonRank;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\ScientificName
	 */
	public function getAcceptedName()
	{
		return $this->acceptedName;
	}

	public function setAcceptedName(ScientificName $scientificName)
	{
		$this->acceptedName = scientificName;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\ScientificName
	 */
	public function getValidName()
	{
		return $this->acceptedName;
	}

	public function setValidName(ScientificName $scientificName)
	{
		$this->acceptedName = scientificName;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\model\DefaultClassification
	 */
	public function getDefaultClassification()
	{
		return $this->defaultClassification;
	}

	public function setDefaultClassification(DefaultClassification $defaultClassification)
	{
		$this->defaultClassification = defaultClassification;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\MonomialList
	 */
	public function getSystemClassification()
	{
		return $this->systemClassification;
	}

	public function setSystemClassification(MonomialList $systemClassification)
	{
		$this->systemClassification = systemClassification;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\SynonymList
	 */
	public function getSynonyms()
	{
		return $this->synonyms;
	}

	public function setSynonyms(SynonymList $synonyms)
	{
		$this->synonyms = synonyms;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\VernacularNameList
	 */
	public function getVernacularNames()
	{
		return $this->vernacularNames;
	}

	public function setVernacularNames(VernacularNameList $vernacularNames)
	{
		$this->vernacularNames = vernacularNames;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\TaxonDescriptionList
	 */
	public function getDescriptions()
	{
		return $this->descriptions;
	}

	public function setDescriptions(TaxonDescriptionList $descriptions)
	{
		$this->descriptions = descriptions;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\ExpertList
	 */
	public function getExperts()
	{
		return $this->experts;
	}

	public function setExperts(ExpertList $experts)
	{
		$this->experts = experts;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\ReferenceList
	 */
	public function getReferences()
	{
		return $this->references;
	}

	public function setReferences(ReferenceList $references)
	{
		$this->references = references;
	}

	/**
	 *
	 * @return nl\naturalis\nba\client\php\helper\SpecimenList
	 */
	public function getSpecimens()
	{
		return $this->specimens;
	}

	public function setSpecimens(SpecimenList $specimens)
	{
		$this->specimens = specimens;
	}
}
