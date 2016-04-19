<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;

class TaxonDescription extends NBADomainObject {

	private $category;
	private $description;
	private $language;

	public function getCategory()
	{
		return $this->category;
	}

	public function setCategory(string $category)
	{
		$this->category = category;
	}

	public function getDescription()
	{
		return $this->description;
	}

	public function setDescription(string $description)
	{
		$this->description = description;
	}

	public function getLanguage()
	{
		return $this->language;
	}

	public function setLanguage(string $language)
	{
		$this->language = language;
	}
}
