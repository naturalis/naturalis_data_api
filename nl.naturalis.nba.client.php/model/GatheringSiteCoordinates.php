<?php

namespace nl\naturalis\nba\client\php\model;

class GatheringSiteCoordinates extends NBADomainObject {
	private $longitudeDecimal;
	private $latitudeDecimal;
	private $gridCellSystem;
	private $gridLatitudeDecimal;
	private $gridLongitudeDecimal;
	private $gridCellCode;
	private $gridQualifier;

	public function __construct(float $latitude, float $longitude)
	{
		$this->longitudeDecimal = $longitude;
		$this->latitudeDecimal = $latitude;
	}

	public function getPoint()
	{
		if ($this->longitudeDecimal == null || $this->latitudeDecimal == null) {
			return null;
		}
		return new GeoPoint(longitudeDecimal, latitudeDecimal);
	}

	public function getLongitudeDecimal()
	{
		return $this->longitudeDecimal;
	}

	public function setLongitudeDecimal(float $longitudeDecimal)
	{
		$this->longitudeDecimal = longitudeDecimal;
	}

	public function getLatitudeDecimal()
	{
		return $this->latitudeDecimal;
	}

	public function setLatitudeDecimal(float $latitudeDecimal)
	{
		$this->latitudeDecimal = latitudeDecimal;
	}

	public function getGridCellSystem()
	{
		return $this->gridCellSystem;
	}

	public function setGridCellSystem(string $gridCellSystem)
	{
		$this->gridCellSystem = gridCellSystem;
	}

	public function getGridLatitudeDecimal()
	{
		return $this->gridLatitudeDecimal;
	}

	public function setGridLatitudeDecimal(float $gridLatitudeDecimal)
	{
		$this->gridLatitudeDecimal = gridLatitudeDecimal;
	}

	public function getGridLongitudeDecimal()
	{
		return $this->gridLongitudeDecimal;
	}

	public function setGridLongitudeDecimal(float $gridLongitudeDecimal)
	{
		$this->gridLongitudeDecimal = gridLongitudeDecimal;
	}

	public function getGridCellCode()
	{
		return $this->gridCellCode;
	}

	public function setGridCellCode(string $gridCellCode)
	{
		$this->gridCellCode = gridCellCode;
	}

	public function getGridQualifier()
	{
		return $this->gridQualifier;
	}

	public function setGridQualifier(string $gridQualifier)
	{
		$this->gridQualifier = gridQualifier;
	}
}
