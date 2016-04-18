<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\model\NBADomainObject;

class Iptc4xmpExt extends NBADomainObject {

	private $locationShown;
	private $worldRegion;
	private $countryCode;
	private $countryName;
	private $provinceState;
	private $city;
	private $sublocation;

	public function __construct()
	{
		parent::__construct();
	}

	public function getLocationShown()
	{
		return $this->locationShown;
	}

	public function setLocationShown(string $locationShown)
	{
		$this->locationShown = $locationShown;
	}

	public function getWorldRegion()
	{
		return $this->worldRegion;
	}

	public function setWorldRegion(string $worldRegion)
	{
		$this->worldRegion = $worldRegion;
	}

	public function getCountryCode()
	{
		return $this->countryCode;
	}

	public function setCountryCode(string $countryCode)
	{
		$this->countryCode = $countryCode;
	}

	public function getCountryName()
	{
		return $this->countryName;
	}

	public function setCountryName(string $countryName)
	{
		$this->countryName = $countryName;
	}

	public function getProvinceState()
	{
		return $this->provinceState;
	}

	public function setProvinceState(string $provinceState)
	{
		$this->provinceState = $provinceState;
	}

	public function getCity()
	{
		return $this->city;
	}

	public function setCity(string $city)
	{
		$this->city = $city;
	}

	public function getSublocation()
	{
		return $this->sublocation;
	}

	public function setSublocation(string $sublocation)
	{
		$this->sublocation = $sublocation;
	}
}
