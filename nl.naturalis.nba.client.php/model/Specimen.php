<?php

namespace nl\naturalis\nba\client\php\model;

use nl\naturalis\nba\client\php\helper\SpecimenIdentificationList;
use nl\naturalis\nba\client\php\helper\SpecimenList;
use nl\naturalis\nba\client\php\helper\TaxonList;
use nl\naturalis\nba\client\php\model\Agent;
use nl\naturalis\nba\client\php\model\GatheringEvent;
use nl\naturalis\nba\client\php\model\INBARecord;
use nl\naturalis\nba\client\php\model\NBATraceableObject;
use nl\naturalis\nba\client\php\model\PhaseOrStage;
use nl\naturalis\nba\client\php\model\Sex;
use nl\naturalis\nba\client\php\model\SpecimenTypeStatus;

class Specimen extends NBATraceableObject implements INBARecord {

	private $id;
	private $unitID;
	private $unitGUID;
	private $collectorsFieldNumber;
	private $assemblageID;
	private $sourceInstitutionID;
	private $sourceID;
	private $owner;
	private $licenseType;
	private $license;
	private $recordBasis;
	private $kindOfUnit;
	private $collectionType;
	private $typeStatus;
	private $sex;
	private $phaseOrStage;
	private $title;
	private $notes;
	private $preparationType;
	private $numberOfSpecimen;
	private $fromCaptivity;
	private $objectPublic;
	private $multiMediaPublic;
	private $acquiredFrom;
	private $gatheringEvent;
	private $identifications;
	private $otherSpecimensInAssemblage;
	private $associatedTaxa;

	public function getId()
	{
		return $this->id;
	}

	public function setId(string $id)
	{
		$this->id = $id;
	}

	public function getUnitID()
	{
		return $this->unitID;
	}

	public function setUnitID(string $unitID)
	{
		$this->unitID = $unitID;
	}

	public function getUnitGUID()
	{
		return $this->unitGUID;
	}

	public function setUnitGUID(string $unitGUID)
	{
		$this->unitGUID = $unitGUID;
	}

	public function getCollectorsFieldNumber()
	{
		return $this->collectorsFieldNumber;
	}

	public function setCollectorsFieldNumber(string $collectorsFieldNumber)
	{
		$this->collectorsFieldNumber = $collectorsFieldNumber;
	}

	public function getAssemblageID()
	{
		return $this->assemblageID;
	}

	public function setAssemblageID(string $assemblageID)
	{
		$this->assemblageID = $assemblageID;
	}

	public function getSourceInstitutionID()
	{
		return $this->sourceInstitutionID;
	}

	public function setSourceInstitutionID(string $sourceInstitutionID)
	{
		$this->sourceInstitutionID = $sourceInstitutionID;
	}

	public function getSourceID()
	{
		return $this->sourceID;
	}

	public function setSourceID(string $sourceID)
	{
		$this->sourceID = $sourceID;
	}

	public function getOwner()
	{
		return $this->owner;
	}

	public function setOwner(string $owner)
	{
		$this->owner = $owner;
	}

	public function getLicenseType()
	{
		return $this->licenseType;
	}

	public function setLicenseType(string $licenseType)
	{
		$this->licenseType = $licenseType;
	}

	public function getLicense()
	{
		return $this->license;
	}

	public function setLicense(string $license)
	{
		$this->license = $license;
	}

	public function getRecordBasis()
	{
		return $this->recordBasis;
	}

	public function setRecordBasis(string $recordBasis)
	{
		$this->recordBasis = $recordBasis;
	}

	public function getKindOfUnit()
	{
		return $this->kindOfUnit;
	}

	public function setKindOfUnit(string $kindOfUnit)
	{
		$this->kindOfUnit = $kindOfUnit;
	}

	public function getCollectionType()
	{
		return $this->collectionType;
	}

	public function setCollectionType(string $collectionType)
	{
		$this->collectionType = $collectionType;
	}

	public function getTypeStatus()
	{
		return $this->typeStatus;
	}

	public function setTypeStatus(SpecimenTypeStatus $typeStatus)
	{
		$this->typeStatus = $typeStatus;
	}

	public function getSex()
	{
		return $this->sex;
	}

	public function setSex(Sex $sex)
	{
		$this->sex = $sex;
	}

	public function getPhaseOrStage()
	{
		return $this->phaseOrStage;
	}

	public function setPhaseOrStage(PhaseOrStage $phaseOrStage)
	{
		$this->phaseOrStage = $phaseOrStage;
	}

	public function getTitle()
	{
		return $this->title;
	}

	public function setTitle(string $title)
	{
		$this->title = $title;
	}

	public function getNotes()
	{
		return $this->notes;
	}

	public function setNotes(string $notes)
	{
		$this->notes = $notes;
	}

	public function getPreparationType()
	{
		return $this->preparationType;
	}

	public function setPreparationType(string $preparationType)
	{
		$this->preparationType = $preparationType;
	}

	public function getNumberOfSpecimen()
	{
		return $this->numberOfSpecimen;
	}

	public function setNumberOfSpecimen(int $numberOfSpecimen)
	{
		$this->numberOfSpecimen = $numberOfSpecimen;
	}

	public function isFromCaptivity()
	{
		return $this->fromCaptivity;
	}

	public function setFromCaptivity(bool $fromCaptivity)
	{
		$this->fromCaptivity = $fromCaptivity;
	}

	public function isObjectPublic()
	{
		return $this->objectPublic;
	}

	public function setObjectPublic($objectPublic)
	{
		$this->objectPublic = $objectPublic;
	}

	public function isMultiMediaPublic()
	{
		return $this->multiMediaPublic;
	}

	public function setMultiMediaPublic($multiMediaPublic)
	{
		$this->multiMediaPublic = $multiMediaPublic;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\Agent
	 */
	public function getAcquiredFrom()
	{
		return $this->acquiredFrom;
	}

	public function setAcquiredFrom(Agent $acquiredFrom)
	{
		$this->acquiredFrom = $acquiredFrom;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\model\GatheringEvent
	 */
	public function getGatheringEvent()
	{
		return $this->gatheringEvent;
	}

	public function setGatheringEvent(GatheringEvent $gatheringEvent)
	{
		$this->gatheringEvent = $gatheringEvent;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\SpecimenIdentificationList
	 */
	public function getIdentifications()
	{
		return $this->identifications;
	}

	public function setIdentifications(SpecimenIdentificationList $identifications)
	{
		$this->identifications = $identifications;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\SpecimenList
	 */
	public function getOtherSpecimensInAssemblage()
	{
		return $this->otherSpecimensInAssemblage;
	}

	public function setOtherSpecimensInAssemblage(SpecimenList $otherSpecimensInAssemblage)
	{
		$this->otherSpecimensInAssemblage = $otherSpecimensInAssemblage;
	}

	/**
	 *
	 * @return \nl\naturalis\nba\client\php\helper\TaxonList
	 */
	public function getAssociatedTaxa()
	{
		return $this->associatedTaxa;
	}

	public function setAssociatedTaxa(TaxonList $associatedTaxa)
	{
		$this->associatedTaxa = $associatedTaxa;
	}
}
