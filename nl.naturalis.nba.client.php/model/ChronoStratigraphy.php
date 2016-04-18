<?php

namespace nl\naturalis\nba\client\php\model;

class ChronoStratigraphy extends NBADomainObject {

	private $youngRegionalSubstage;
	private $youngRegionalStage;
	private $youngRegionalSeries;
	private $youngDatingQualifier;
	private $youngInternSystem;
	private $youngInternSubstage;
	private $youngInternStage;
	private $youngInternSeries;
	private $youngInternErathem;
	private $youngInternEonothem;
	private $youngChronoName;
	private $youngCertainty;
	private $oldDatingQualifier;
	private $chronoPreferredFlag;
	private $oldRegionalSubstage;
	private $oldRegionalStage;
	private $oldRegionalSeries;
	private $oldInternSystem;
	private $oldInternSubstage;
	private $oldInternStage;
	private $oldInternSeries;
	private $oldInternErathem;
	private $oldInternEonothem;
	private $oldChronoName;
	private $chronoIdentifier;
	private $oldCertainty;

	public function __construct()
	{
		parent::__construct ();
	}

	public function getYoungRegionalSubstage()
	{
		return $this->youngRegionalSubstage;
	}

	public function setYoungRegionalSubstage(string $youngRegionalSubstage)
	{
		$this->youngRegionalSubstage = youngRegionalSubstage;
	}

	public function getYoungRegionalStage()
	{
		return $this->youngRegionalStage;
	}

	public function setYoungRegionalStage(string $youngRegionalStage)
	{
		$this->youngRegionalStage = youngRegionalStage;
	}

	public function getYoungRegionalSeries()
	{
		return $this->youngRegionalSeries;
	}

	public function setYoungRegionalSeries(string $youngRegionalSeries)
	{
		$this->youngRegionalSeries = youngRegionalSeries;
	}

	public function getYoungDatingQualifier()
	{
		return $this->youngDatingQualifier;
	}

	public function setYoungDatingQualifier(string $youngDatingQualifier)
	{
		$this->youngDatingQualifier = youngDatingQualifier;
	}

	public function getYoungInternSystem()
	{
		return $this->youngInternSystem;
	}

	public function setYoungInternSystem(string $youngInternSystem)
	{
		$this->youngInternSystem = youngInternSystem;
	}

	public function getYoungInternSubstage()
	{
		return $this->youngInternSubstage;
	}

	public function setYoungInternSubstage(string $youngInternSubstage)
	{
		$this->youngInternSubstage = youngInternSubstage;
	}

	public function getYoungInternStage()
	{
		return $this->youngInternStage;
	}

	public function setYoungInternStage(string $youngInternStage)
	{
		$this->youngInternStage = youngInternStage;
	}

	public function getYoungInternSeries()
	{
		return $this->youngInternSeries;
	}

	public function setYoungInternSeries(string $youngInternSeries)
	{
		$this->youngInternSeries = youngInternSeries;
	}

	public function getYoungInternErathem()
	{
		return $this->youngInternErathem;
	}

	public function setYoungInternErathem(string $youngInternErathem)
	{
		$this->youngInternErathem = youngInternErathem;
	}

	public function getYoungInternEonothem()
	{
		return $this->youngInternEonothem;
	}

	public function setYoungInternEonothem(string $youngInternEonothem)
	{
		$this->youngInternEonothem = youngInternEonothem;
	}

	public function getYoungChronoName()
	{
		return $this->youngChronoName;
	}

	public function setYoungChronoName(string $youngChronoName)
	{
		$this->youngChronoName = youngChronoName;
	}

	public function getYoungCertainty()
	{
		return $this->youngCertainty;
	}

	public function setYoungCertainty(string $youngCertainty)
	{
		$this->youngCertainty = youngCertainty;
	}

	public function getOldDatingQualifier()
	{
		return $this->oldDatingQualifier;
	}

	public function setOldDatingQualifier(string $oldDatingQualifier)
	{
		$this->oldDatingQualifier = oldDatingQualifier;
	}

	public function isChronoPreferredFlag()
	{
		return $this->chronoPreferredFlag;
	}

	public function setChronoPreferredFlag(bool $chronoPreferredFlag)
	{
		$this->chronoPreferredFlag = chronoPreferredFlag;
	}

	public function getOldRegionalSubstage()
	{
		return $this->oldRegionalSubstage;
	}

	public function setOldRegionalSubstage(string $oldRegionalSubstage)
	{
		$this->oldRegionalSubstage = oldRegionalSubstage;
	}

	public function getOldRegionalStage()
	{
		return $this->oldRegionalStage;
	}

	public function setOldRegionalStage(string $oldRegionalStage)
	{
		$this->oldRegionalStage = oldRegionalStage;
	}

	public function getOldRegionalSeries()
	{
		return $this->oldRegionalSeries;
	}

	public function setOldRegionalSeries(string $oldRegionalSeries)
	{
		$this->oldRegionalSeries = oldRegionalSeries;
	}

	public function getOldInternSystem()
	{
		return $this->oldInternSystem;
	}

	public function setOldInternSystem(string $oldInternSystem)
	{
		$this->oldInternSystem = oldInternSystem;
	}

	public function getOldInternSubstage()
	{
		return $this->oldInternSubstage;
	}

	public function setOldInternSubstage(string $oldInternSubstage)
	{
		$this->oldInternSubstage = oldInternSubstage;
	}

	public function getOldInternStage()
	{
		return $this->oldInternStage;
	}

	public function setOldInternStage(string $oldInternStage)
	{
		$this->oldInternStage = oldInternStage;
	}

	public function getOldInternSeries()
	{
		return $this->oldInternSeries;
	}

	public function setOldInternSeries(string $oldInternSeries)
	{
		$this->oldInternSeries = oldInternSeries;
	}

	public function getOldInternErathem()
	{
		return $this->oldInternErathem;
	}

	public function setOldInternErathem(string $oldInternErathem)
	{
		$this->oldInternErathem = oldInternErathem;
	}

	public function getOldInternEonothem()
	{
		return $this->oldInternEonothem;
	}

	public function setOldInternEonothem(string $oldInternEonothem)
	{
		$this->oldInternEonothem = oldInternEonothem;
	}

	public function getOldChronoName()
	{
		return $this->oldChronoName;
	}

	public function setOldChronoName(string $oldChronoName)
	{
		$this->oldChronoName = oldChronoName;
	}

	public function getChronoIdentifier()
	{
		return $this->chronoIdentifier;
	}

	public function setChronoIdentifier(string $chronoIdentifier)
	{
		$this->chronoIdentifier = chronoIdentifier;
	}

	public function getOldCertainty()
	{
		return $this->oldCertainty;
	}

	public function setOldCertainty(string $oldCertainty)
	{
		$this->oldCertainty = oldCertainty;
	}
}
