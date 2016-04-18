<?php

namespace nl\naturalis\nba\client\php\model;

class BioStratigraphy extends NBADomainObject {

	private $youngBioDatingQualifier;
	private $youngBioName;
	private $youngFossilZone;
	private $youngFossilSubZone;
	private $youngBioCertainty;
	private $youngStratType;
	private $bioDatingQualifier;
	private $bioPreferredFlag;
	private $rangePosition;
	private $oldBioName;
	private $bioIdentifier;
	private $oldFossilzone;
	private $oldFossilSubzone;
	private $oldBioCertainty;
	private $oldBioStratType;

	public function __construct()
	{
		parent::__construct ();
	}

	public function getYoungBioDatingQualifier()
	{
		return $this->youngBioDatingQualifier;
	}

	public function setYoungBioDatingQualifier(string $youngBioDatingQualifier)
	{
		$this->youngBioDatingQualifier = $youngBioDatingQualifier;
	}

	public function getYoungBioName()
	{
		return $this->youngBioName;
	}

	public function setYoungBioName(string $youngBioName)
	{
		$this->youngBioName = $youngBioName;
	}

	public function getYoungFossilZone()
	{
		return $this->youngFossilZone;
	}

	public function setYoungFossilZone(string $youngFossilZone)
	{
		$this->youngFossilZone = $youngFossilZone;
	}

	public function getYoungFossilSubZone()
	{
		return $this->youngFossilSubZone;
	}

	public function setYoungFossilSubZone(string $youngFossilSubZone)
	{
		$this->youngFossilSubZone = $youngFossilSubZone;
	}

	public function getYoungBioCertainty()
	{
		return $this->youngBioCertainty;
	}

	public function setYoungBioCertainty(string $youngBioCertainty)
	{
		$this->youngBioCertainty = $youngBioCertainty;
	}

	public function getYoungStratType()
	{
		return $this->youngStratType;
	}

	public function setYoungStratType(string $youngStratType)
	{
		$this->youngStratType = $youngStratType;
	}

	public function getBioDatingQualifier()
	{
		return $this->bioDatingQualifier;
	}

	public function setBioDatingQualifier(string $bioDatingQualifier)
	{
		$this->bioDatingQualifier = $bioDatingQualifier;
	}

	public function isBioPreferredFlag()
	{
		return $this->bioPreferredFlag;
	}

	public function setBioPreferredFlag(bool $bioPreferredFlag)
	{
		$this->bioPreferredFlag = $bioPreferredFlag;
	}

	public function getRangePosition()
	{
		return $this->rangePosition;
	}

	public function setRangePosition(string $rangePosition)
	{
		$this->rangePosition = $rangePosition;
	}

	public function getOldBioName()
	{
		return $this->oldBioName;
	}

	public function setOldBioName(string $oldBioName)
	{
		$this->oldBioName = $oldBioName;
	}

	public function getBioIdentifier()
	{
		return $this->bioIdentifier;
	}

	public function setBioIdentifier(string $bioIdentifier)
	{
		$this->bioIdentifier = $bioIdentifier;
	}

	public function getOldFossilzone()
	{
		return $this->oldFossilzone;
	}

	public function setOldFossilzone(string $oldFossilzone)
	{
		$this->oldFossilzone = $oldFossilzone;
	}

	public function getOldFossilSubzone()
	{
		return $this->oldFossilSubzone;
	}

	public function setOldFossilSubzone(string $oldFossilSubzone)
	{
		$this->oldFossilSubzone = $oldFossilSubzone;
	}

	public function getOldBioCertainty()
	{
		return $this->oldBioCertainty;
	}

	public function setOldBioCertainty(string $oldBioCertainty)
	{
		$this->oldBioCertainty = $oldBioCertainty;
	}

	public function getOldBioStratType()
	{
		return $this->oldBioStratType;
	}

	public function setOldBioStratType(string $oldBioStratType)
	{
		$this->oldBioStratType = $oldBioStratType;
	}
}
