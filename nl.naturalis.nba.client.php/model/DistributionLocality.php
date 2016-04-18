<?php

namespace nl\naturalis\nba\client\php\model;

class DistributionLocality extends NBADomainObject {

	private $locality;

	public function getLocality()
	{
		return $this->locality;
	}

	public function setLocality(string $locality)
	{
		$this->locality = $locality;
	}
}
