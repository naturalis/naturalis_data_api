<?php

namespace nl\naturalis\nba\client\php\model;

use \DateTime;

class GatheringEvent extends NBADomainObject {

	private $projectTitle;
	private $worldRegion;
	private $continent;
	private $country;
	private $iso3166Code;
	private $provinceState;
	private $island;
	private $locality;
	private $city;
	private $sublocality;
	private $localityText;
	private $dateTimeBegin;
	private $dateTimeEnd;
	private $method;
	private $altitude;
	private $altitudeUnifOfMeasurement;
	private $depth;
	private $depthUnitOfMeasurement;
	private $gatheringAgents;
	private $siteCoordinates;
	private $chronoStratigraphy;
	private $bioStratigraphic;
	private $lithoStratigraphy;

	public function addGatheringAgent(Agent $agent)
	{
		if (gatheringAgents == null) {
			$this->gatheringAgents = array();
		}
		$this->gatheringAgents[] = $agent;
	}

	public function addSiteCoordinates(GatheringSiteCoordinates $coordinates)
	{
		if (siteCoordinates == null) {
			$this->siteCoordinates = array();
		}
		$this->siteCoordinates[] = $coordinates;
	}

	public function addSiteCoordinates(float $latitude, float $longitude)
	{
		$this->addSiteCoordinates(new GatheringSiteCoordinates(latitude, longitude));
	}

	public function getProjectTitle()
	{
		return $this->projectTitle;
	}

	public function setProjectTitle(string $projectTitle)
	{
		$this->projectTitle = $projectTitle;
	}

	public function getWorldRegion()
	{
		return $this->worldRegion;
	}

	public function setWorldRegion(string $worldRegion)
	{
		$this->worldRegion = $worldRegion;
	}

	public function getContinent()
	{
		return $this->continent;
	}

	public function setContinent(string $continent)
	{
		$this->continent = $continent;
	}

	public function getCountry()
	{
		return $this->country;
	}

	public function setCountry(string $country)
	{
		$this->country = $country;
	}

	public function getIso3166Code()
	{
		return $this->iso3166Code;
	}

	public function setIso3166Code(string $iso3166Code)
	{
		$this->iso3166Code = $iso3166Code;
	}

	public function getProvinceState()
	{
		return $this->provinceState;
	}

	public function setProvinceState(string $provinceState)
	{
		$this->provinceState = $provinceState;
	}

	public function getIsland()
	{
		return $this->island;
	}

	public function setIsland(string $island)
	{
		$this->island = $island;
	}

	public function getLocality()
	{
		return $this->locality;
	}

	public function setLocality(string $locality)
	{
		$this->locality = $locality;
	}

	public function getCity()
	{
		return $this->city;
	}

	public function setCity(string $city)
	{
		$this->city = $city;
	}

	public function getSublocality()
	{
		return $this->sublocality;
	}

	public function setSublocality(string $sublocality)
	{
		$this->sublocality = $sublocality;
	}

	public function getLocalityText()
	{
		return $this->localityText;
	}

	public function setLocalityText(string $localityText)
	{
		$this->localityText = $localityText;
	}

	public function getDateTimeBegin()
	{
		return $this->dateTimeBegin;
	}

	public function setDateTimeBegin(DateTime $dateTimeBegin)

	{
		$this->dateTimeBegin = $dateTimeBegin;
	}

	public function getDateTimeEnd()
	{
		return $this->dateTimeEnd;
	}

	public function setDateTimeEnd(DateTime $dateTimeEnd)
	{
		$this->dateTimeEnd = $dateTimeEnd;
	}

	public function getMethod()
	{
		return $this->method;
	}

	public function setMethod(string $method)
	{
		$this->method = $method;
	}

	public function getAltitude()
	{
		return $this->altitude;
	}

	public function setAltitude(string $altitude)
	{
		$this->altitude = $altitude;
	}

	public function getAltitudeUnifOfMeasurement()
	{
		return $this->altitudeUnifOfMeasurement;
	}

	public function setAltitudeUnifOfMeasurement(string $altitudeUnifOfMeasurement)
	{
		$this->altitudeUnifOfMeasurement = $altitudeUnifOfMeasurement;
	}

	public function getDepth()
	{
		return $this->depth;
	}

	public function setDepth(string $depth)
	{
		$this->depth = $depth;
	}

	public function getDepthUnitOfMeasurement()
	{
		return $this->depthUnitOfMeasurement;
	}

	public function setDepthUnitOfMeasurement(string $depthUnitOfMeasurement)
	{
		$this->depthUnitOfMeasurement = $depthUnitOfMeasurement;
	}

	public function getGatheringAgents()
	{
		return $this->gatheringAgents;
	}

	public function setGatheringAgents(array $gatheringAgents)
	{
		$this->gatheringAgents = $gatheringAgents;
	}

	public function getSiteCoordinates()
	{
		return $this->siteCoordinates;
	}

	public function setSiteCoordinates(array $siteCoordinates)
	{
		$this->siteCoordinates = $siteCoordinates;
	}

	public function getChronoStratigraphy()
	{
		return $this->chronoStratigraphy;
	}

	public function setChronoStratigraphy($chronoStratigraphy)
	{
		$this->chronoStratigraphy = $chronoStratigraphy;
	}

	public function getBiostratigraphic()
	{
		return $this->bioStratigraphic;
	}

	public function setBiostratigraphic($bioStratigraphic)
	{
		$this->bioStratigraphic = $bioStratigraphic;
	}

	public function getLithoStratigraphy()
	{
		return $this->lithoStratigraphy;
	}

	public function setLithoStratigraphy($lithoStratigraphy)
	{
		$this->lithoStratigraphy = $lithoStratigraphy;
	}
}
