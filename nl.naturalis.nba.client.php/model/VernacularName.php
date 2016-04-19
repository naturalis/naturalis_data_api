<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;
use nl\naturalis\nba\client\php\helper\ReferenceList;
use nl\naturalis\nba\client\php\helper\ExpertList;

class VernacularName extends NBADomainObject {

	private $name;
	private $language;
	private $preferred;
	private $references;
	private $experts;

	public function __construct(string $name)
	{
		$this->name = $name;
	}

	public function getName()
	{
		return $this->name;
	}

	public function setName(string $name)
	{
		$this->name = $name;
	}

	public function getLanguage()
	{
		return $this->language;
	}

	public function setLanguage(string $language)
	{
		$this->language = $language;
	}

	public function getPreferred()
	{
		return $this->preferred;
	}

	public function setPreferred(bool $preferred)
	{
		$this->preferred = $preferred;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\ReferenceList
	 */
	public function getReferences()
	{
		return $this->references;
	}

	public function setReferences(ReferenceList $references)
	{
		$this->references = $references;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\ExpertList
	 */
	public function getExperts()
	{
		return $this->experts;
	}

	public function setExperts(ExpertList $experts)
	{
		$this->experts = $experts;
	}
}
